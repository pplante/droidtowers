/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.events.CollectionChangeEvent;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class HappyDroidServiceCollection<ApiType extends HappyDroidServiceObject> {
  private static final ApiCollectionRunnable NO_OP_API_RUNNABLE = new ApiCollectionRunnable();

  private Metadata meta;
  private List<ApiType> objects;
  private HashMap<String, String> currentFilters;
  private EventBus eventBus;

  public HappyDroidServiceCollection() {
    objects = Lists.newArrayList();
    currentFilters = Maps.newHashMap();
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
    if (!HappyDroidService.instance().haveNetworkConnection()) {
      apiRunnable.onError(null, HttpStatusCode.ClientClosedRequest, this);
      return;
    }

    HttpResponse response = HappyDroidService.instance().makeGetRequest(getBaseResourceUri(), currentFilters);
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

  public HappyDroidServiceCollection<ApiType> filterBy(final String fieldName, final String filterValue) {
    if (currentFilters == null) {
      currentFilters = new HashMap<String, String>();
    }

    currentFilters.put(fieldName, filterValue);

    return this;
  }

  public HappyDroidServiceCollection<ApiType> filterBy(final String fieldName, long filterValue) {
    return filterBy(fieldName, "" + filterValue);
  }

  public void fetch() {
    fetch(NO_OP_API_RUNNABLE);
  }

  public void add(ApiType object) {
    for (ApiType apiType : objects) {
      if (apiType.getResourceUri() != null && apiType.getResourceUri().equals(object.getResourceUri())) {
        return;
      }
    }

    objects.add(object);

    if (eventBus != null) {
      eventBus.post(new CollectionChangeEvent(object));
    }
  }

  public EventBus events() {
    if (eventBus == null) {
      eventBus = new EventBus();
    }

    return eventBus;
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
