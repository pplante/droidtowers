/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.jackson.TowerGameClassDeserializer;
import com.happydroids.droidtowers.jackson.Vector2Serializer;
import com.happydroids.droidtowers.jackson.Vector3Serializer;
import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.UUID;

public class TowerGameService extends HappyDroidService {
  private static final String TAG = TowerGameService.class.getSimpleName();

  private Preferences preferences;
  private String deviceId;
  private boolean isAuthenticated;

  public TowerGameService() {
    super();

    preferences = Gdx.app.getPreferences("CONNECT");
    if (!preferences.contains("DEVICE_ID")) {
      preferences.putString("DEVICE_ID", UUID.randomUUID().toString().replaceAll("-", ""));
      preferences.flush();
    }
    deviceId = preferences.getString("DEVICE_ID");

    getObjectMapper().registerSubtypes(new NamedType(GameSave.class, "com.unhappyrobot.gamestate.GameSave"));
    getObjectMapper().addDeserializer(Class.class, new TowerGameClassDeserializer());
    getObjectMapper().addSerializer(new Vector3Serializer());
    getObjectMapper().addSerializer(new Vector2Serializer());
  }

  public static TowerGameService instance() {
    if (_instance == null) {
      _instance = new TowerGameService();
    }

    return (TowerGameService) _instance;
  }

  public void registerDevice() {
    withNetworkConnection(new Runnable() {
      public void run() {
        Device device = new Device();
        device.save(new ApiRunnable<Device>() {
          @Override
          public void onError(HttpResponse response, int statusCode, Device object) {
            Gdx.app.error(TAG, "Error registering device :(, status: " + statusCode);
          }

          @Override
          public void onSuccess(HttpResponse response, Device object) {
            isAuthenticated = object.isAuthenticated;

            if (!isAuthenticated) {
              preferences.remove("SESSION_TOKEN");
              preferences.flush();
            }
          }
        });
      }
    });
  }

  public String getSessionToken() {
    return preferences.getString("SESSION_TOKEN", null);
  }

  public String getDeviceId() {
    return deviceId;
  }

  public synchronized void setSessionToken(String token) {
    preferences.putString("SESSION_TOKEN", token);
    preferences.flush();
  }

  public boolean hasAuthenticated() {
    return isAuthenticated;
  }

  @Override
  protected void addDefaultHeaders(HttpRequestBase request) {
    super.addDefaultHeaders(request);

    request.setHeader("X-Device-UUID", HappyDroidService.instance().getDeviceId());
    if (getSessionToken() != null) {
      request.setHeader("X-Token", getSessionToken());
    }
  }
}
