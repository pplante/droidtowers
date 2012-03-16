package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
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
        String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        System.out.println("\tResponse: " + content);
        return mapper.readValue(content, aClazz);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
