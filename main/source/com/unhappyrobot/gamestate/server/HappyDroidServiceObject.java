package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class HappyDroidServiceObject {
  protected HappyDroidServiceObject() {

  }

  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = new ObjectMapper();
    if (response != null) {
      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        String content = EntityUtils.toString(entity);
        System.out.println("\tResponse: " + content);
        return mapper.readValue(content, aClazz);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
