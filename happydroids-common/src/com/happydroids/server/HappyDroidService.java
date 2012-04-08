/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Sets;
import com.happydroids.HappyDroidConsts;
import com.happydroids.utils.BackgroundTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HappyDroidService {
  private static final String TAG = HappyDroidService.class.getSimpleName();
  protected static HappyDroidService _instance;
  private static String deviceType;
  private static String deviceOSVersion;

  protected boolean hasNetworkConnection;
  private final Set<Runnable> withNetworkConnectionRunnables = Sets.newHashSet();

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
    checkForNetwork();
  }

  protected HappyDroidService(int fuckYouJava) {
    // leave this for tests.
  }

  public static void setDeviceOSName(String deviceType) {
    HappyDroidService.deviceType = deviceType;
  }

  public static void setDeviceOSVersion(String deviceOSVersion) {
    HappyDroidService.deviceOSVersion = deviceOSVersion;
  }

  public static String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public static String getDeviceType() {
    return deviceType;
  }

  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule("Specials");
    addCustomSerializers(simpleModule);
    objectMapper.registerModule(simpleModule);
    return objectMapper;
  }

  protected void addCustomSerializers(SimpleModule simpleModule) {
    simpleModule.addSerializer(new StackTraceSerializer());
  }

  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = instance().getObjectMapper();
    if (response != null) {
      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        if (entity != null && entity.getContentLength() > 0) {
          String content = EntityUtils.toString(entity, HTTP.UTF_8);
//          System.out.println("\tResponse: " + content);
          return mapper.readValue(content, aClazz);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public HttpResponse makePutRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      System.out.println("PUT " + uri);
      HttpPut request = new HttpPut(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = getObjectMapper();
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
        ObjectMapper mapper = getObjectMapper();
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

  protected void addDefaultHeaders(HttpRequestBase request) {
    request.setHeader("Content-Type", "application/json");
  }

  public HttpResponse makeGetRequest(String uri) {
    HttpClient client = new DefaultHttpClient();
    try {
      HttpGet request = new HttpGet(uri);
      addDefaultHeaders(request);
      System.out.println("REQ: GET " + uri);
      HttpResponse response = client.execute(request);
      System.out.println("RES: GET " + uri + ", " + response.getStatusLine());
      return response;
    } catch (HttpHostConnectException ignored) {
      System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public void checkForNetwork() {
    Logger.getLogger(TAG).info("Checking for network connection...");

    new BackgroundTask() {
      @Override
      public void execute() {
        try {
          InetAddress remote = InetAddress.getByName(HappyDroidConsts.HAPPYDROIDS_SERVER);
          hasNetworkConnection = remote.isReachable(1500);
          Logger.getLogger(TAG).info("Network status: " + hasNetworkConnection);
          synchronized (withNetworkConnectionRunnables) {
            for (Runnable runnable : withNetworkConnectionRunnables) {
              runnable.run();
            }

            withNetworkConnectionRunnables.clear();
          }
        } catch (IOException e) {
          Logger.getLogger(TAG).log(Level.ALL, "Network error!", e);
        }
      }
    }.run();
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

  public String getDeviceId() {
    return null;
  }

  public boolean hasAuthenticated() {
    return false;
  }
}
