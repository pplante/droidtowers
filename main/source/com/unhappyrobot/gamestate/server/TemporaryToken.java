package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.http.HttpRequest;
import com.unhappyrobot.http.HttpResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TemporaryToken {
  private int value;
  private String resource_uri;
  private SessionToken session;

  public static TemporaryToken create() {
    try {
      HttpResponse response = HttpRequest.makeRequest(HttpRequest.REQUEST_TYPE.GET, "http://127.0.0.1:8000/api/v1/temporarytoken/create-token/?format=json");
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(response.getBodyString(), TemporaryToken.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public TemporaryToken() {
  }

  public int getValue() {
    return value;
  }

  public String getSessionToken() {
    return session.token;
  }

  public boolean hasSessionToken() {
    return session != null && session.token != null;
  }

  public boolean validate() {
    try {
      HttpResponse response = HttpRequest.makeRequest(HttpRequest.REQUEST_TYPE.GET, "http://127.0.0.1:8000" + resource_uri);
      ObjectMapper mapper = new ObjectMapper();
      TemporaryToken temporaryToken = mapper.readValue(response.getBodyString(), TemporaryToken.class);
      if (temporaryToken.hasSessionToken()) {
        session = temporaryToken.session;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return hasSessionToken();
  }

  public class SessionToken {
    public String token;
  }


}
