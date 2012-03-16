package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporaryToken {
  private String value;
  private String resource_uri;
  private String clickable_uri;
  private SessionToken session;

  private TemporaryToken() {
  }

  public static TemporaryToken create() {
    HttpResponse response = HappyDroidService.instance().makePostRequest(Consts.API_V1_TEMPORARY_TOKEN_CREATE, new HashMap<String, String>());
    return HappyDroidServiceObject.materializeObject(response, TemporaryToken.class);
  }

  public boolean validate() {
    HttpResponse response = HappyDroidService.instance().makeGetRequest(Consts.HAPPYDROIDS_SERVER + resource_uri);
    TemporaryToken token = HappyDroidServiceObject.materializeObject(response, TemporaryToken.class);
    if (token != null && token.hasSessionToken()) {
      session = token.session;
      HappyDroidService.instance().setSessionToken(session.token);
    }

    return hasSessionToken();
  }

  public String getValue() {
    return value;
  }

  public String getSessionToken() {
    return session.token;
  }

  public boolean hasSessionToken() {
    return session != null && session.token != null;
  }

  public String getClickableUri() {
    return clickable_uri;
  }

  public class SessionToken {
    public String token;

    @Override
    public String toString() {
      return "SessionToken{" +
                     "token='" + token + '\'' +
                     '}';
    }
  }

  @Override
  public String toString() {
    return "TemporaryToken{" +
                   "resource_uri='" + resource_uri + '\'' +
                   ", value=" + value +
                   ", session=" + session +
                   '}';
  }
}
