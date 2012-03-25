package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.unhappyrobot.utils.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HappyDroidService {
  private static HappyDroidService _instance;
  private String deviceType;
  private String deviceOSVersion;
  private Preferences preferences;
  private String deviceId;
  private boolean isAuthenticated;
  private boolean hasNetworkConnection;
  private final Set<Runnable> withNetworkConnectionRunnables;

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

    withNetworkConnectionRunnables = Sets.newHashSet();
    checkForNetwork();
  }

  public void registerDevice() {
    HashMap<String, String> deviceInfo = Maps.newHashMap();
    deviceInfo.put("uuid", HappyDroidService.instance().getDeviceId());
    deviceInfo.put("type", HappyDroidService.instance().getDeviceType());
    deviceInfo.put("osVersion", HappyDroidService.instance().getDeviceOSVersion());
    HttpResponse response = HappyDroidService.instance().makePostRequest(Consts.API_V1_REGISTER_DEVICE, deviceInfo);

    if (response != null && response.getStatusLine().getStatusCode() == 201) {
      HashMap hashMap = HappyDroidServiceObject.materializeObject(response, HashMap.class);
      if (hashMap.containsKey("isAuthenticated")) {
        isAuthenticated = (Boolean) hashMap.get("isAuthenticated");
        if (!isAuthenticated) {
          preferences.remove("SESSION_TOKEN");
          preferences.flush();
        }
      }
    } else {
      isAuthenticated = false;
    }
  }

  public boolean makePutRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      System.out.println("PUT " + uri);
      HttpPut request = new HttpPut(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = new ObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(objectForServer));
        entity.setContentType("multipart/form-data");
        request.setEntity(entity);
      }

      HttpResponse response = client.execute(request);
      System.out.println("\t" + response.getStatusLine());

      if (response.getStatusLine().getStatusCode() == 204) {
        return true;
      } else {
        String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        System.out.println("\tResponse: " + content);
      }
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
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
      System.out.println("\t" + response.getStatusLine());

      if (response.getStatusLine().getStatusCode() == 500) {
        String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        System.out.println("\tResponse: " + content);
      }

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

  public void checkForNetwork() {
    new AsyncTask() {
      @Override
      public void execute() {
        try {
          InetAddress remote = InetAddress.getByName(Consts.HAPPYDROIDS_SERVER);
          hasNetworkConnection = remote.isReachable(1500);

          synchronized (withNetworkConnectionRunnables) {
            for (Runnable runnable : withNetworkConnectionRunnables) {
              runnable.run();
            }

            withNetworkConnectionRunnables.clear();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.run();
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

  public void withNetworkConnection(Runnable runnable) {
    if (hasNetworkConnection) {
      runnable.run();
    } else {
      withNetworkConnectionRunnables.add(runnable);
    }
  }
}
