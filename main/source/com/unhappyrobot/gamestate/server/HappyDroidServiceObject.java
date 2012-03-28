package com.unhappyrobot.gamestate.server;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.jackson.Vector2Serializer;
import com.unhappyrobot.jackson.Vector3Serializer;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import java.io.IOException;
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

  protected String resourceUri;

  protected abstract String getResourceBaseUri();

  protected HappyDroidServiceObject() {

  }

  public String getResourceUri() {
    return resourceUri;
  }

  protected abstract boolean requireAuthentication();

  public void setResourceUri(String resourceUri) {
    this.resourceUri = resourceUri;
  }

  public static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule("Specials", new Version(1, 0, 0, null));
    simpleModule.addSerializer(new Vector3Serializer());
    simpleModule.addSerializer(new Vector2Serializer());
    simpleModule.addSerializer(new StackTraceSerializer());
    objectMapper.registerModule(simpleModule);
    return objectMapper;
  }

  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = getObjectMapper();
    if (response != null) {
      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        if (entity != null && entity.getContentLength() > 0) {
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

    HttpResponse response = HappyDroidService.instance().makeGetRequest(TowerConsts.HAPPYDROIDS_URI + resourceUri);
    if (response != null && response.getStatusLine().getStatusCode() == 200) {
      copyValuesFromResponse(response);
    }
  }

  public void save() {
    save(DUMMY_AFTER_SAVE);
  }

  @SuppressWarnings("unchecked")
  public void save(ApiRunnable afterSave) {
    if (!HappyDroidService.instance().haveNetworkConnection()) {
      afterSave.onError(null, HttpStatusCode.ClientClosedRequest, this);
      return;
    } else if (requireAuthentication() && !HappyDroidService.instance().hasAuthenticated()) {
      afterSave.onError(null, HttpStatusCode.NetworkAuthenticationRequired, this);
      return;
    }

    HttpResponse response;
    if (resourceUri == null) {
      response = HappyDroidService.instance().makePostRequest(getResourceBaseUri(), this);
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
}
