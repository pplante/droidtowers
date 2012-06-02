/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

    getObjectMapper().addDeserializer(Class.class, new TowerGameClassDeserializer());
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

  public void registerDevice() {
    withNetworkConnection(new Runnable() {
      public void run() {
        device = new Device();
        device.save(new ApiRunnable<Device>() {
          @Override
          public void onError(HttpResponse response, int statusCode, Device object) {
            Gdx.app.error(TAG, "Error registering device :(, status: " + statusCode);
            authenticationFinished = true;
            postAuthRunnables.runAll();
          }

          @Override
          public void onSuccess(HttpResponse response, Device object) {
            authenticationFinished = true;

            authenticated = object.isAuthenticated;

            if (!authenticated) {
              preferences.remove(SESSION_TOKEN);
              preferences.flush();
            }

            postAuthRunnables.runAll();
          }
        });
      }
    });
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

  public void setAudioEnabled(boolean audioEnabled) {
    preferences.putBoolean("audioEnabled", audioEnabled);
    preferences.flush();
  }

  public boolean isAudioEnabled() {
    return preferences.getBoolean("audioEnabled", true);
  }
}
