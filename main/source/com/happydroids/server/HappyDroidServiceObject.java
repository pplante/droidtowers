/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.Platform;
import org.apach3.http.Header;
import org.apach3.http.HttpResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonFilter(value = "HappyDroidServiceObject")
public abstract class HappyDroidServiceObject {
  public static final ApiRunnable NO_OP_API_RUNNABLE = new ApiRunnable();

  private long id;
  private String resourceUri;
  private boolean fetchError;


  @JsonIgnore
  public abstract String getBaseResourceUri();

  protected HappyDroidServiceObject() {

  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getResourceUri() {
    return resourceUri;
  }

  protected abstract boolean requireAuthentication();

  public void setResourceUri(String resourceUri) {
    if (resourceUri != null && !resourceUri.startsWith("http")) {
      resourceUri = HappyDroidConsts.HAPPYDROIDS_URI + resourceUri;
    }

    this.resourceUri = resourceUri;
  }

  private void validateResourceUri() {
    if (resourceUri != null && !resourceUri.startsWith("http")) {
      resourceUri = HappyDroidConsts.HAPPYDROIDS_URI + resourceUri;
    }
  }

  public void fetch(final ApiRunnable apiRunnable) {
    if (resourceUri == null) {
      throw new RuntimeException("resourceUri must not be null when using fetch()");
    }

    fetchError = true;

    validateResourceUri();

    HttpResponse response = HappyDroidService.instance()
                                    .makeGetRequest(resourceUri, null, isCachingAllowed(), getCacheMaxAge());
    if (response != null && response.getStatusLine().getStatusCode() == 200) {
      fetchError = false;
      copyValuesFromResponse(response);
    }

    apiRunnable.handleResponse(response, HappyDroidServiceObject.this);
  }

  protected int getCacheMaxAge() {
    return -1;
  }

  protected boolean isCachingAllowed() {
    return false;
  }

  public void save() {
    save(NO_OP_API_RUNNABLE);
  }

  @SuppressWarnings("unchecked")
  public void save(final ApiRunnable apiRunnable) {
    try {
      if (!Platform.getConnectionMonitor().isConnectedOrConnecting()) {
        apiRunnable.onError(null, HttpStatusCode.ClientClosedRequest, this);
        return;
      } else if (!beforeSaveValidation(apiRunnable)) {
        apiRunnable.onError(null, HttpStatusCode.ClientValidationFailed, this);
        return;
      }

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

      apiRunnable.handleResponse(response, HappyDroidServiceObject.this);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  @SuppressWarnings("unchecked")
  protected boolean beforeSaveValidation(ApiRunnable afterSave) {
    if (!Platform.getConnectionMonitor().isConnectedOrConnecting()) {
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

  public void fetch() {
    fetch(NO_OP_API_RUNNABLE);
  }

  public boolean errorWhileFetching() {
    return fetchError;
  }
}
