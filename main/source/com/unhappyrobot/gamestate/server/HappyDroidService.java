package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unhappyrobot.gamestate.GameSave;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class HappyDroidService {
  private static HappyDroidService _instance;
  private String deviceType;
  private String deviceOSVersion;
  private Preferences preferences;
  private String deviceId;
  private boolean isAuthenticated;

  public static HappyDroidService instance() {
    if (_instance == null) {
      _instance = new HappyDroidService();
    }

    return _instance;
  }

  public static void setInstance(HappyDroidService instance) {
    HappyDroidService._instance = instance;
  }

  protected HappyDroidService() {
    preferences = Gdx.app.getPreferences("CONNECT");
    if (!preferences.contains("DEVICE_ID")) {
      preferences.putString("DEVICE_ID", UUID.randomUUID().toString().replaceAll("-", ""));
      preferences.flush();
    }

    deviceId = preferences.getString("DEVICE_ID");
  }

  public void registerDevice() {
    HashMap<String, String> deviceInfo = Maps.newHashMap();
    deviceInfo.put("uuid", HappyDroidService.instance().getDeviceId());
    deviceInfo.put("type", HappyDroidService.instance().getDeviceType());
    deviceInfo.put("os_version", HappyDroidService.instance().getDeviceOSVersion());
    HttpResponse response = HappyDroidService.instance().makePostRequest(Consts.API_V1_REGISTER_DEVICE, deviceInfo);

    if (response.getStatusLine().getStatusCode() == 201) {
      HashMap hashMap = HappyDroidServiceObject.materializeObject(response, HashMap.class);
      if (hashMap.containsKey("is_authed")) {
        isAuthenticated = (Boolean) hashMap.get("is_authed");
        if (!isAuthenticated) {
          preferences.remove("SESSION_TOKEN");
          preferences.flush();
        }
      }
    } else {
      isAuthenticated = false;
    }
  }

  public String uploadGameSave(GameSave gameSave) {
    if (!isAuthenticated) {
      return null;
    }

    String gameSaveUri = Consts.API_V1_GAMESAVE_LIST;
    if (gameSave.getCloudSaveUri() != null) {
      gameSaveUri = Consts.HAPPYDROIDS_SERVER + gameSave.getCloudSaveUri();
    }

    HttpResponse response = makePostRequest(gameSaveUri, new CloudGameSave(gameSave));
    if (response != null) {

      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        String content = EntityUtils.toString(entity, HTTP.UTF_8);
        System.out.println("\tResponse: " + content);
      } catch (IOException e) {
        e.printStackTrace();
      }
      Header location = Iterables.getFirst(Lists.newArrayList(response.getHeaders("Location")), null);
      if (location != null) {
        return location.toString();
      }
    }
    return null;
  }

  public HttpResponse makePostRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      System.out.println("POST " + uri);
      HttpPost request = new HttpPost(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = new ObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(objectForServer));
        entity.setContentType("multipart/form-data");
        request.setEntity(entity);
      }

      HttpResponse response = client.execute(request);
      System.out.println(response.getStatusLine());
      System.out.println("response.getHeaders() = " + Arrays.toString(response.getAllHeaders()));
      return response;
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private void addDefaultHeaders(HttpRequestBase request) {
    request.setHeader("Content-Type", "application/json");
    request.setHeader("X-Device-UUID", HappyDroidService.instance().getDeviceId());
    if (getSessionToken() != null) {
      request.setHeader("X-Token", getSessionToken());
    }
  }

  public HttpResponse makeGetRequest(String uri) {
    HttpClient client = new DefaultHttpClient();
    try {
      System.out.println("GET " + uri);
      HttpGet request = new HttpGet(uri);
      addDefaultHeaders(request);

      HttpResponse response = client.execute(request);
      System.out.println(response.getStatusLine());
      return response;
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public String getSessionToken() {
    return preferences.getString("SESSION_TOKEN", null);
  }

  public void setDeviceOSName(String deviceType) {
    this.deviceType = deviceType;
  }

  public void setDeviceOSVersion(String deviceOSVersion) {
    this.deviceOSVersion = deviceOSVersion;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public synchronized void setSessionToken(String token) {
    preferences.putString("SESSION_TOKEN", token);
    preferences.flush();
  }
}
