package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.utils.BackgroundTask;
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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

public class HappyDroidService {
  private static final String TAG = HappyDroidService.class.getSimpleName();
  private static HappyDroidService _instance;
  private static String deviceType;
  private static String deviceOSVersion;
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

  public HttpResponse makePutRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      System.out.println("PUT " + uri);
      HttpPut request = new HttpPut(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = HappyDroidServiceObject.getObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(objectForServer));
        entity.setContentType("multipart/form-data");
        request.setEntity(entity);
      }

      return client.execute(request);
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
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
        ObjectMapper mapper = HappyDroidServiceObject.getObjectMapper();
        System.out.println(mapper.writeValueAsString(objectForServer));
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
      HttpGet request = new HttpGet(uri);
      addDefaultHeaders(request);

      HttpResponse response = client.execute(request);
      System.out.println("GET " + uri + ", " + response.getStatusLine());
      return response;
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public void checkForNetwork() {
    Gdx.app.debug(TAG, "Checking for network connection...");
    new BackgroundTask() {
      @Override
      public void execute() {
        try {
          InetAddress remote = InetAddress.getByName(TowerConsts.HAPPYDROIDS_SERVER);
          hasNetworkConnection = remote.isReachable(1500);
          Gdx.app.debug(TAG, "Network status: " + hasNetworkConnection);
          synchronized (withNetworkConnectionRunnables) {
            for (Runnable runnable : withNetworkConnectionRunnables) {
              runnable.run();
            }

            withNetworkConnectionRunnables.clear();
          }
        } catch (IOException e) {
          Gdx.app.debug(TAG, "Network error!", e);
        }
      }
    }.run();
  }

  public String getSessionToken() {
    return preferences.getString("SESSION_TOKEN", null);
  }

  public static void setDeviceOSName(String deviceType) {
    HappyDroidService.deviceType = deviceType;
  }

  public static void setDeviceOSVersion(String deviceOSVersion) {
    HappyDroidService.deviceOSVersion = deviceOSVersion;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public static String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public static String getDeviceType() {
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

  public boolean haveNetworkConnection() {
    return hasNetworkConnection;
  }

  public boolean hasAuthenticated() {
    return isAuthenticated;
  }
}
