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
      HttpResponse response = HttpRequest.makeRequest(HttpRequest.REQUEST_TYPE.GET, Consts.API_V1_TEMPORARYTOKEN_CREATE_TOKEN);
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(response.getBodyString(), TemporaryToken.class);
    } catch (IOException ignored) {
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
      HttpResponse response = HttpRequest.makeRequest(HttpRequest.REQUEST_TYPE.GET, Consts.HAPPYDROIDS_SERVER + resource_uri + "?format=json");
      ObjectMapper mapper = new ObjectMapper();
      TemporaryToken temporaryToken = mapper.readValue(response.getBodyString(), TemporaryToken.class);
      if (temporaryToken.hasSessionToken()) {
        session = temporaryToken.session;
      }
    } catch (IOException ignored) {
    }

    return hasSessionToken();
  }

  public class SessionToken {
    public String token;
  }


}
