package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerConsts;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporaryToken extends HappyDroidServiceObject {
  private String value;
  private String clickableUri;
  private SessionToken session;

  @Override
  protected String getResourceBaseUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/temporarytoken/";
  }

  public TemporaryToken() {
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  public boolean validate() {
    reload();
    return hasSessionToken();
  }

  public String getValue() {
    return value;
  }

  public String getSessionToken() {
    return session != null ? session.token : null;
  }

  public boolean hasSessionToken() {
    return session != null && session.token != null;
  }

  public String getClickableUri() {
    return clickableUri;
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
                   "resourceUri='" + resourceUri + '\'' +
                   ", value=" + value +
                   ", session=" + session +
                   '}';
  }
}
