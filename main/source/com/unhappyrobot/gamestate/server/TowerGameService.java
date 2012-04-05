/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidService;
import com.unhappyrobot.jackson.Vector2Serializer;
import com.unhappyrobot.jackson.Vector3Serializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.UUID;

public class TowerGameService extends HappyDroidService {
  private static final String TAG = TowerGameService.class.getSimpleName();

  private static String deviceType;
  private static String deviceOSVersion;
  private Preferences preferences;
  private String deviceId;
  private boolean isAuthenticated;

  protected TowerGameService() {
    super();

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

  public static void setDeviceOSName(String deviceType) {
    TowerGameService.deviceType = deviceType;
  }

  public static void setDeviceOSVersion(String deviceOSVersion) {
    TowerGameService.deviceOSVersion = deviceOSVersion;
  }

  public static String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public static String getDeviceType() {
    return deviceType;
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
  protected void addCustomSerializers(SimpleModule simpleModule) {
    simpleModule.addSerializer(new Vector3Serializer());
    simpleModule.addSerializer(new Vector2Serializer());
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
