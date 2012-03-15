package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporaryToken {
  private long value;
  private String check_token_uri;
  private SessionToken session;

  private TemporaryToken() {
  }

  public static TemporaryToken create() {
    HashMap<String, String> map = new HashMap<String, String>();

    HttpResponse response = HappyDroidService.instance().makePostRequest(Consts.API_V1_TEMPORARY_TOKEN_CREATE, map);

    return HappyDroidServiceObject.materializeObject(response, TemporaryToken.class);
  }

  public boolean validate() {
    HttpResponse response = HappyDroidService.instance().makeGetRequest(Consts.HAPPYDROIDS_SERVER + check_token_uri);
    TemporaryToken token = HappyDroidServiceObject.materializeObject(response, TemporaryToken.class);
    if (token != null && token.hasSessionToken()) {
      session = token.session;
      HappyDroidService.instance().setSessionToken(session.token);
    }

    return hasSessionToken();
  }

  public long getValue() {
    return value;
  }

  public String getSessionToken() {
    return session.token;
  }

  public boolean hasSessionToken() {
    return session != null && session.token != null;
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
                   "check_token_uri='" + check_token_uri + '\'' +
                   ", value=" + value +
                   ", session=" + session +
                   '}';
  }
}
