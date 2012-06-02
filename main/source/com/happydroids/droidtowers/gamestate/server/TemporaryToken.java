/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.happydroids.droidtowers.TowerConsts;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemporaryToken extends TowerGameServiceObject {
  private String value;
  private String clickableUri;
  private SessionToken session;

  @Override
  public String getBaseResourceUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/temporarytoken/";
  }

  public TemporaryToken() {
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  public boolean validate() {
    fetch(NO_OP_API_RUNNABLE);
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
                   "resourceUri='" + getResourceUri() + '\'' +
                   ", value=" + value +
                   ", session=" + session +
                   '}';
  }
}
