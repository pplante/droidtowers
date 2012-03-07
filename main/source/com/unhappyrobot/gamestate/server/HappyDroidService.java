package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unhappyrobot.gamestate.GameSave;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.UUID;

public class HappyDroidService {
  private static HappyDroidService _instance;
  private String deviceType;
  private String deviceOSVersion;
  private Preferences preferences;
  private String deviceId;

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

  public String uploadGameSave(GameSave gameSave) {
    String gameSaveUri = Consts.API_V1_GAMESAVE_LIST;
    if (gameSave.getCloudSaveUri() != null) {
      gameSaveUri = Consts.HAPPYDROIDS_SERVER + gameSave.getCloudSaveUri();
    }

    HttpResponse response = makePostRequest(gameSaveUri, new CloudGameSave(gameSave));
    if (response != null) {
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
      HttpPost request = new HttpPost(uri);
      request.setHeader("Content-Type", "application/json");
      if (getSessionToken() != null) {
        request.setHeader("X-Token", getSessionToken());
      }

      ObjectMapper mapper = new ObjectMapper();
      request.setEntity(new StringEntity(mapper.writeValueAsString(objectForServer), ContentType.MULTIPART_FORM_DATA));

      return client.execute(request);
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.getConnectionManager().shutdown();
    }

    return null;
  }

  public HttpResponse makeGetRequest(String uri) {
    HttpClient client = new DefaultHttpClient();
    try {
      HttpGet request = new HttpGet(uri);
      request.setHeader("Content-Type", "application/json");
      if (getSessionToken() != null) {
        request.setHeader("X-Token", getSessionToken());
      }

      return client.execute(request);
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.getConnectionManager().shutdown();
    }

    return null;
  }

  public String getSessionToken() {
    return preferences.getString("SESSION_TOKEN");
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
}
