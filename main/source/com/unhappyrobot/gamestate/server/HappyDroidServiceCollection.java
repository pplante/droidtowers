package com.unhappyrobot.gamestate.server;

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
  private final Class<ApiType> objectClazz;

  public HappyDroidServiceCollection(Class<ApiType> objectClazz) {
    this.objectClazz = objectClazz;
    objects = null;
  }

  public void fetch(final ApiCollectionRunnable<HappyDroidServiceCollection<ApiType>> apiRunnable) {
    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        HttpResponse response = HappyDroidService.instance().makeGetRequest(getBaseResourceUri());
        if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
          ObjectMapper objectMapper = HappyDroidServiceObject.getObjectMapper();
          try {
            copyValuesFromResponse(response);
          } catch (IOException e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
          }
        }

        apiRunnable.handleResponse(response, HappyDroidServiceCollection.this);
      }
    });
  }

  private void copyValuesFromResponse(HttpResponse response) throws IOException {
    HappyDroidServiceCollection collection = HappyDroidServiceObject.getObjectMapper().readValue(new BufferedHttpEntity(response.getEntity()).getContent(), getClass());
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
