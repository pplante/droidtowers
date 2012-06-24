/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.happydroids.HappyDroidConsts;
import com.happydroids.jackson.HappyDroidObjectMapper;
import com.happydroids.tasks.CheckForNetworkTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class HappyDroidService {
  private static final String TAG = HappyDroidService.class.getSimpleName();
  protected static HappyDroidService _instance;
  private static String deviceType;
  private static String deviceOSVersion;

  protected static boolean hasNetworkConnection;
  private final Set<Runnable> withNetworkConnectionRunnables = Sets.newHashSet();

  protected HappyDroidObjectMapper objectMapper;

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

  public static void setConnectionState(boolean connectionState) {
    HappyDroidService.hasNetworkConnection = connectionState;
  }

  public HappyDroidObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      objectMapper = new HappyDroidObjectMapper();
    }

    return objectMapper;
  }

  public static <T> T materializeObject(HttpResponse response, Class<T> aClazz) {
    ObjectMapper mapper = instance().getObjectMapper();
    if (response != null) {
      try {
        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        if (entity != null && entity.getContentLength() > 0) {
          byte[] content = EntityUtils.toByteArray(entity);
          if (HappyDroidConsts.DEBUG)
            System.out.println("\tResponse: " + Arrays.toString(content));
          return mapper.readValue(content, aClazz);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return null;
  }

  public HttpResponse makePutRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      if (HappyDroidConsts.DEBUG)
        System.out.println("PUT " + uri);
      HttpPut request = new HttpPut(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = getObjectMapper();
        String apiObjectAsString = mapper.writeValueAsString(objectForServer);
        StringEntity entity = new StringEntity(apiObjectAsString);
        if (HappyDroidConsts.DEBUG)
          System.out.println("HTTP ENTITY: " + apiObjectAsString);
        entity.setContentType("multipart/form-data");
        request.setEntity(entity);
      }

      HttpResponse response = client.execute(request);
      Gdx.app.debug(TAG, "\t" + response.getStatusLine());
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 201 && statusCode != 200) {
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
          String content = EntityUtils.toString(responseEntity);
          Gdx.app.debug(TAG, "\tResponse: " + content);
        } else {
          Gdx.app.debug(TAG, "\tResponse: NULL");
        }
      }
      return response;
    } catch (HttpHostConnectException ignored) {
      if (HappyDroidConsts.DEBUG)
        System.out.println("Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public HttpResponse makePostRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      Gdx.app.debug(TAG, "POST " + uri);
      HttpPost request = new HttpPost(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = getObjectMapper();
        Gdx.app.debug(TAG, "JSON: " + mapper.writeValueAsString(objectForServer));
        StringEntity entity = new StringEntity(mapper.writeValueAsString(objectForServer));
        entity.setContentType("multipart/form-data");
        request.setEntity(entity);
      }

      HttpResponse response = client.execute(request);
      Gdx.app.debug(TAG, "RES: " + response.getStatusLine());

      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 201 && statusCode != 200) {
        String content = EntityUtils.toString(response.getEntity());
        Gdx.app.debug(TAG, "\tResponse: " + content);
      }

      return response;
    } catch (HttpHostConnectException ignored) {
      Gdx.app.debug(TAG, "Connection failed for: " + uri);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  protected void addDefaultHeaders(HttpRequestBase request) {
    request.setHeader("Content-Type", "application/json");
  }

  public HttpResponse makeGetRequest(String uri, HashMap<String, String> queryParams) {
    HttpClient client = new DefaultHttpClient();
    try {
      HttpGet request = new HttpGet(uri);
      if (queryParams != null && queryParams.size() > 0) {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(request.getURI().getScheme());
        uriBuilder.setHost(request.getURI().getHost());
        uriBuilder.setPath(request.getURI().getPath());
        uriBuilder.setQuery(request.getURI().getQuery());
        for (String paramName : queryParams.keySet()) {
          uriBuilder.addParameter(paramName, queryParams.get(paramName));
        }

        request.setURI(uriBuilder.build());
      }

      addDefaultHeaders(request);
      Gdx.app.debug(TAG, "REQ: GET " + request.getURI());
      HttpResponse response = client.execute(request);
      Gdx.app.debug(TAG, "RES: GET " + request.getURI() + ", " + response.getStatusLine());
      return response;
    } catch (Exception ignored) {
      Gdx.app.error(TAG, "Connection failed for: " + uri, ignored);
    }

    return null;
  }

  public void checkForNetwork() {
    Logger.getLogger(TAG).info("Checking for network connection...");

    new CheckForNetworkTask().run();
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

  public boolean isAuthenticated() {
    return false;
  }

  public void runAfterNetworkCheckRunnables() {
    synchronized (withNetworkConnectionRunnables) {
      for (Runnable runnable : withNetworkConnectionRunnables) {
        runnable.run();
      }

      withNetworkConnectionRunnables.clear();
    }
  }
}
