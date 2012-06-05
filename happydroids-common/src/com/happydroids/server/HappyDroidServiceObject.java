/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.utils.BackgroundTask;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonIgnoreProperties(value = {"id"})
public abstract class HappyDroidServiceObject {
  public static final ApiRunnable NO_OP_API_RUNNABLE = new ApiRunnable() {
    @Override
    void handleResponse(HttpResponse response, HappyDroidServiceObject ignored) {
      // stub this so we do not waste cycles.
    }
  };

  private String resourceUri;

  @JsonIgnore
  public abstract String getBaseResourceUri();

  protected HappyDroidServiceObject() {

  }

  public String getResourceUri() {
    return resourceUri;
  }

  protected abstract boolean requireAuthentication();

  public void setResourceUri(String resourceUri) {
    if (!resourceUri.contains(HappyDroidConsts.HAPPYDROIDS_URI)) {
      resourceUri = HappyDroidConsts.HAPPYDROIDS_URI + resourceUri;
    }

    this.resourceUri = resourceUri;
  }

  private void validateResourceUri() {
    if (resourceUri != null && !resourceUri.startsWith(HappyDroidConsts.HAPPYDROIDS_URI)) {
      resourceUri = HappyDroidConsts.HAPPYDROIDS_URI + resourceUri;
    }
  }

  public void fetch(final ApiRunnable apiRunnable) {
    if (resourceUri == null) {
      throw new RuntimeException("resourceUri must not be null when using fetch()");
    }

    validateResourceUri();

    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        apiRunnable.handleResponse(fetchBlocking(), HappyDroidServiceObject.this);
      }
    });
  }

  public HttpResponse fetchBlocking() {
    HttpResponse response = HappyDroidService.instance().makeGetRequest(resourceUri, null);
    if (response != null && response.getStatusLine().getStatusCode() == 200) {
      copyValuesFromResponse(response);
    }

    return response;
  }

  public void save() {
    save(NO_OP_API_RUNNABLE);
  }

  @SuppressWarnings("unchecked")
  public void save(final ApiRunnable afterSave) {
    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        new BackgroundTask() {
          private HttpResponse httpResponse;

          @Override
          protected void execute() throws Exception {
            httpResponse = saveBlocking(afterSave);
          }

          @Override
          public synchronized void afterExecute() {
            if (httpResponse != null) {
              afterSave.handleResponse(httpResponse, HappyDroidServiceObject.this);
            }
          }
        }.run();
      }
    });
  }

  public void saveBlocking() {
    HttpResponse response = saveBlocking(NO_OP_API_RUNNABLE);
    if (response != null) {
      NO_OP_API_RUNNABLE.handleResponse(response, this);
    }
  }

  public HttpResponse saveBlocking(ApiRunnable afterSave) {
    if (!beforeSaveValidation(afterSave)) return null;

    validateResourceUri();

    HttpResponse response;
    if (resourceUri == null) {
      response = HappyDroidService.instance().makePostRequest(getBaseResourceUri(), this);
      if (response != null && response.getStatusLine().getStatusCode() == 201) {
        Header location = Iterables.getFirst(Lists.newArrayList(response.getHeaders("Location")), null);
        if (location != null) {
          resourceUri = location.getValue();
        }

        copyValuesFromResponse(response);
      }
    } else {
      response = HappyDroidService.instance().makePutRequest(resourceUri, this);
    }

    return response;
  }

  @SuppressWarnings("unchecked")
  protected boolean beforeSaveValidation(ApiRunnable afterSave) {
    if (!HappyDroidService.instance().haveNetworkConnection()) {
      afterSave.onError(null, HttpStatusCode.ClientClosedRequest, this);
      return false;
    }

    return true;
  }

  private void copyValuesFromResponse(HttpResponse response) {
    HappyDroidServiceObject serverInstance = HappyDroidService.materializeObject(response, getClass());
    if (serverInstance != null) {
      Class<?> currentClass = serverInstance.getClass();

      do {
        for (Field field : currentClass.getDeclaredFields()) {
          copyValueFromField(serverInstance, field);
        }

        currentClass = currentClass.getSuperclass();
      } while (!currentClass.equals(Object.class));
    }
  }

  private void copyValueFromField(HappyDroidServiceObject serverInstance, Field field) {
    try {
      if (!Modifier.isFinal(field.getModifiers())) {
        field.setAccessible(true);
        field.set(this, field.get(serverInstance));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @JsonIgnore
  public boolean isSaved() {
    return resourceUri != null;
  }

  protected ObjectMapper getObjectMapper() {
    return HappyDroidService.instance().getObjectMapper();
  }
}
