/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class HappyDroidServiceObject {
  private static final ApiRunnable DUMMY_AFTER_SAVE = new ApiRunnable() {
    @Override
    void handleResponse(HttpResponse response, HappyDroidServiceObject ignored) {
      // stub this so we do not waste cycles.
    }
  };

  protected int id;
  protected String resourceUri;

  public abstract String getBaseResourceUri();

  protected HappyDroidServiceObject() {

  }

  public String getResourceUri() {
    return resourceUri;
  }

  protected abstract boolean requireAuthentication();

  public void setResourceUri(String resourceUri) {
    this.resourceUri = resourceUri;
  }

  public void reload() {
    if (resourceUri == null) {
      throw new RuntimeException("resourceUri must not be null when using reload()");
    }

    HttpResponse response = HappyDroidService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + resourceUri);
    if (response != null && response.getStatusLine().getStatusCode() == 200) {
      copyValuesFromResponse(response);
    }
  }

  public void save() {
    save(DUMMY_AFTER_SAVE);
  }

  @SuppressWarnings("unchecked")
  public void save(final ApiRunnable afterSave) {
    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        saveBlocking(afterSave);
      }
    });
  }

  public void saveBlocking() {
    saveBlocking(DUMMY_AFTER_SAVE);
  }

  public void saveBlocking(ApiRunnable afterSave) {
    if (!beforeSaveValidation(afterSave)) return;

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


    afterSave.handleResponse(response, this);
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

  public boolean isSaved() {
    return resourceUri != null;
  }

  protected ObjectMapper getObjectMapper() {
    return HappyDroidService.instance().getObjectMapper();
  }
}
