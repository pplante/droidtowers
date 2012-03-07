package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class HappyDroidServiceObject {
  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = new ObjectMapper();
    if (response != null) {
      try {
        return mapper.readValue(EntityUtils.toString(response.getEntity()), aClazz);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
