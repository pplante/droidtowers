/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.events.CollectionChangeEvent;
import org.apach3.http.HttpResponse;
import org.apach3.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class HappyDroidServiceCollection<CollectionType extends HappyDroidServiceObject> {
  private static final ApiCollectionRunnable NO_OP_API_RUNNABLE = new ApiCollectionRunnable();

  private Metadata meta;
  private List<CollectionType> objects;
  private HashMap<String, String> currentFilters;
  private EventBus eventBus;
  private boolean fetching;

  public HappyDroidServiceCollection() {
    objects = Lists.newArrayList();
    currentFilters = Maps.newHashMap();
  }

  public void fetch(final ApiCollectionRunnable<HappyDroidServiceCollection<CollectionType>> apiRunnable) {
    fetchBlocking(apiRunnable);
  }

  private void copyValuesFromResponse(HttpResponse response) throws IOException {
    HappyDroidServiceCollection collection = HappyDroidService.instance()
                                                     .getObjectMapper()
                                                     .readValue(new BufferedHttpEntity(response.getEntity()).getContent(), getClass());
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

  public List<CollectionType> getObjects() {
    return objects;
  }

  public boolean isEmpty() {
    return objects == null || objects.isEmpty();
  }

  public void fetchBlocking(ApiCollectionRunnable<HappyDroidServiceCollection<CollectionType>> apiRunnable) {
    fetching = true;
    HttpResponse response = HappyDroidService.instance()
                                    .makeGetRequest(getBaseResourceUri(), currentFilters, isCachingAllowed(), getCacheMaxAge());
    fetching = false;
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

  protected int getCacheMaxAge() {
    return -1;
  }

  protected boolean isCachingAllowed() {
    return false;
  }

  public HappyDroidServiceCollection<CollectionType> filterBy(final String fieldName, final String filterValue) {
    if (currentFilters == null) {
      currentFilters = new HashMap<String, String>();
    }

    currentFilters.put(fieldName, filterValue);

    return this;
  }

  public HappyDroidServiceCollection<CollectionType> filterBy(final String fieldName, long filterValue) {
    return filterBy(fieldName, "" + filterValue);
  }

  public void fetch() {
    fetch(NO_OP_API_RUNNABLE);
  }

  public void add(CollectionType object) {
    for (CollectionType collectionType : objects) {
      if (collectionType.getResourceUri() != null && collectionType.getResourceUri().equals(object.getResourceUri())) {
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
      eventBus = new SafeEventBus();
    }

    return eventBus;
  }

  public boolean isFetching() {
    return fetching;
  }

  public int size() {
    return objects.size();
  }

  public void clear() {
    objects.clear();
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
