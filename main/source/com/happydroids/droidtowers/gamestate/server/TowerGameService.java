/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.happydroids.droidtowers.jackson.Vector2Serializer;
import com.happydroids.droidtowers.jackson.Vector3Serializer;
import com.happydroids.server.HappyDroidService;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.UUID;

public class TowerGameService extends HappyDroidService {
  private static final String TAG = TowerGameService.class.getSimpleName();
  public static final String SESSION_TOKEN = "SESSION_TOKEN";

  private Preferences preferences;
  private String deviceId;
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

    initializePreferences();
  }

  public void initializePreferences() {
    preferences = Gdx.app.getPreferences("CONNECT");
    if (!preferences.contains("DEVICE_ID")) {
      preferences.putString("DEVICE_ID", UUID.randomUUID().toString().replaceAll("-", ""));
      preferences.flush();
    }
    deviceId = preferences.getString("DEVICE_ID");
  }

  public static TowerGameService instance() {
    if (_instance == null) {
      _instance = new TowerGameService();
    }

    return (TowerGameService) _instance;
  }

  public String getSessionToken() {
    return preferences.getString(SESSION_TOKEN, null);
  }

  public String getDeviceId() {
    return deviceId;
  }

  public synchronized void setSessionToken(String token) {
    if (token != null) {
      preferences.putString(SESSION_TOKEN, token);
      preferences.flush();

      authenticationFinished = true;
      authenticated = true;
      postAuthRunnables.runAll();
    } else {
      preferences.remove(SESSION_TOKEN);
      preferences.flush();
    }
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  protected void addDefaultHeaders(HttpRequestBase request) {
    super.addDefaultHeaders(request);

    request.setHeader("X-Device-UUID", HappyDroidService.instance().getDeviceId());
    if (getSessionToken() != null) {
      request.setHeader("X-Token", getSessionToken());
    }
  }

  public void afterAuthentication(Runnable runnable) {
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
    preferences.putBoolean("audioState", audioEnabled);
    preferences.flush();
  }

  public boolean getAudioState() {
    return preferences.getBoolean("audioState", true);
  }

  public RunnableQueue getPostAuthRunnables() {
    return postAuthRunnables;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;

    if (!this.authenticated) {
      preferences.remove(SESSION_TOKEN);
      preferences.flush();
    }

    authenticationFinished = true;
    postAuthRunnables.runAll();
  }
}
