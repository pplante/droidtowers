/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class HappyDroidServiceCollection<ApiType extends HappyDroidServiceObject> {
  private Metadata meta;
  private List<ApiType> objects;

  public HappyDroidServiceCollection(Class<ApiType> objectClazz) {
    objects = null;
  }

  public void fetch(final ApiCollectionRunnable<HappyDroidServiceCollection<ApiType>> apiRunnable) {
    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        fetchBlocking(apiRunnable);
      }
    });
  }

  private void copyValuesFromResponse(HttpResponse response) throws IOException {
    HappyDroidServiceCollection collection = HappyDroidService.instance().getObjectMapper().readValue(new BufferedHttpEntity(response.getEntity()).getContent(), getClass());
    if (collection != null) {
      Class<?> currentClass = collection.getClass();

      do {
        for (Field field : currentClass.getDeclaredFields()) {
          copyValueFromField(collection, field);
        }

        currentClass = currentClass.getSuperclass();
      } while (!currentClass.equals(Object.class));
    }
  }

  private void copyValueFromField(Object serverInstance, Field field) {
    try {
      if (!Modifier.isFinal(field.getModifiers())) {
        field.setAccessible(true);
        field.set(this, field.get(serverInstance));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract boolean requireAuthentication();

  public abstract String getBaseResourceUri();

  public List<ApiType> getObjects() {
    return objects;
  }

  public boolean isEmpty() {
    return objects == null || objects.isEmpty();
  }

  public void fetchBlocking(ApiCollectionRunnable<HappyDroidServiceCollection<ApiType>> apiRunnable) {
    HttpResponse response = HappyDroidService.instance().makeGetRequest(getBaseResourceUri());
    if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
      ObjectMapper objectMapper = HappyDroidService.instance().getObjectMapper();
      try {
        copyValuesFromResponse(response);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    apiRunnable.handleResponse(response, this);
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Metadata {
    public int limit;
    public int offset;
    public int totalCount;
    public String next;
    public String previous;

    public Metadata() {
    }
  }
}
