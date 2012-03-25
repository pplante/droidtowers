package com.unhappyrobot.gamestate.server;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class HappyDroidServiceObject {
  protected String resourceUri;

  protected abstract String getResourceBaseUri();

  protected HappyDroidServiceObject() {

  }

  public String getResourceUri() {
    return resourceUri;
  }

  public void setResourceUri(String resourceUri) {
    this.resourceUri = resourceUri;
  }

  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = new ObjectMapper();
    if (response != null) {
      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        if (entity != null) {
          String content = EntityUtils.toString(entity, HTTP.UTF_8);
          System.out.println("\tResponse: " + content);
          return mapper.readValue(content, aClazz);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public void reload() {
    if (resourceUri == null) {
      throw new RuntimeException("resourceUri must not be null when using reload()");
    }

    HttpResponse response = HappyDroidService.instance().makeGetRequest(Consts.HAPPYDROIDS_URI + resourceUri);
    if (response != null && response.getStatusLine().getStatusCode() == 200) {
      HappyDroidServiceObject serverInstance = materializeObject(response, getClass());
      if (serverInstance != null) {
        copyValuesFromResponse(response);
      }
    }
  }

  public void save() {
    save(null);
  }

  public void save(ApiRunnable afterSave) {
    if (resourceUri == null) {
      HttpResponse response = HappyDroidService.instance().makePostRequest(getResourceBaseUri(), this);
      if (response != null && response.getStatusLine().getStatusCode() == 201) {
        Header location = Iterables.getFirst(Lists.newArrayList(response.getHeaders("Location")), null);
        if (location != null) {
          resourceUri = location.getValue();
        }

        copyValuesFromResponse(response);
      }
    } else {
      HappyDroidService.instance().makePutRequest(resourceUri, this);
    }
  }

  private void copyValuesFromResponse(HttpResponse response) {
    HappyDroidServiceObject serverInstance = materializeObject(response, getClass());
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
      field.setAccessible(true);
      field.set(this, field.get(serverInstance));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isSaved() {
    return resourceUri != null;
  }
}
