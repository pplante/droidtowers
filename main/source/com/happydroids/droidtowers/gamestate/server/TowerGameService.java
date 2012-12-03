/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.jackson.Vector2Serializer;
import com.happydroids.droidtowers.jackson.Vector3Serializer;
import com.happydroids.security.SecurePreferences;
import com.happydroids.server.HappyDroidService;
import org.apach3.http.client.methods.HttpRequestBase;

import java.util.UUID;

public class TowerGameService extends HappyDroidService {
  private static final String TAG = TowerGameService.class.getSimpleName();
  public static final String SESSION_TOKEN = "SESSION_TOKEN";
  public static final String DEVICE_ID = "DEVICE_ID";

  private SecurePreferences preferences;
  private boolean authenticated;
  private Device device;
  private RunnableQueue postAuthRunnables;
  private boolean authenticationFinished;

  public TowerGameService() {
    super();

    postAuthRunnables = new RunnableQueue();

//    getObjectMapper().addDeserializer(Class.class, new TowerGameClassDeserializer());
    getObjectMapper().addSerializer(new Vector3Serializer());
    getObjectMapper().addSerializer(new Vector2Serializer());
  }

  public static TowerGameService instance() {
    if (_instance == null) {
      _instance = new TowerGameService();
    }

    return (TowerGameService) _instance;
  }


  public static boolean hasBeenInitialised() {
    return _instance != null;
  }

  public String getSessionToken() {
    return getPreferences().getString(SESSION_TOKEN, null);
  }

  public String getDeviceId() {
    return getPreferences().getString(DEVICE_ID);
  }

  public synchronized void setSessionToken(String token) {
    if (token != null) {
      getPreferences().putString(SESSION_TOKEN, token);
      getPreferences().flush();

      authenticationFinished = true;
      authenticated = true;
      postAuthRunnables.runAll();
    } else {
      getPreferences().remove(SESSION_TOKEN);
      getPreferences().flush();
    }
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  protected void addDefaultHeaders(HttpRequestBase request) {
    super.addDefaultHeaders(request);

    request.setHeader("X-Device-UUID", HappyDroidService.instance().getDeviceId());
    request.setHeader("X-Device-Market", HappyDroidService.getDeviceOSMarketName());
    if (getSessionToken() != null) {
      request.setHeader("X-Token", getSessionToken());
    }
  }

  public void afterDeviceIdentification(Runnable runnable) {
    if (!authenticationFinished) {
      postAuthRunnables.push(runnable);
    } else {
      runnable.run();
    }
  }

  public void resetAuthentication() {
    setSessionToken(null);

    authenticated = false;
    authenticationFinished = false;
    postAuthRunnables.clear();
  }

  public void setAudioState(boolean audioEnabled) {
    getPreferences().putBoolean("audioState", audioEnabled);
    getPreferences().flush();
  }

  public boolean getAudioState() {
    return getPreferences().getBoolean("audioState", true);
  }

  public RunnableQueue getPostAuthRunnables() {
    return postAuthRunnables;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;

    if (!this.authenticated) {
      setSessionToken(null);
    }

    authenticationFinished = true;
    postAuthRunnables.runAll();
  }

  public void setDeviceId(String deviceId) {
    getPreferences().putString(DEVICE_ID, deviceId);
    getPreferences().flush();
  }

  public SecurePreferences getPreferences() {
    if (preferences == null) {
      preferences = new SecurePreferences("com.happydroids.droidtowers." + getDeviceOSMarketName());
      if (!preferences.contains(DEVICE_ID)) {
        preferences.putString(DEVICE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
        preferences.flush();
      }

      VibrateClickListener.setVibrateEnabled(preferences.getBoolean("vibrateOnTouch", true));
    }

    return preferences;
  }
}
