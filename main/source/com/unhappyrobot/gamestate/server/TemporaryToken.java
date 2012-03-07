package com.unhappyrobot.gamestate.server;

import com.google.common.collect.Maps;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporaryToken extends HappyDroidServiceObject {
  private int value;
  private String check_token_uri;
  private SessionToken session;

  private TemporaryToken() {
  }

  public static TemporaryToken create() {
    HashMap<String, String> deviceInfo = Maps.newHashMap();
    deviceInfo.put("uuid", HappyDroidService.instance().getDeviceId());
    deviceInfo.put("type", HappyDroidService.instance().getDeviceType());
    deviceInfo.put("os_version", HappyDroidService.instance().getDeviceOSVersion());
    HttpResponse response = HappyDroidService.instance().makePostRequest(Consts.API_V1_TEMPORARYTOKEN_CREATE_TOKEN, deviceInfo);

    return materializeObject(response, TemporaryToken.class);
  }

  public boolean validate() {
    HttpResponse response = HappyDroidService.instance().makeGetRequest(Consts.HAPPYDROIDS_SERVER + check_token_uri);
    TemporaryToken token = materializeObject(response, TemporaryToken.class);
    if (token != null && token.hasSessionToken()) {
      session = token.session;
    }

    return hasSessionToken();
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

  private class SessionToken {
    public String token;
  }
}
