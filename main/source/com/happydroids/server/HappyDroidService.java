/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happydroids.HappyDroidConsts;
import com.happydroids.jackson.HappyDroidObjectMapper;
import org.apach3.http.HttpEntity;
import org.apach3.http.HttpResponse;
import org.apach3.http.NameValuePair;
import org.apach3.http.client.HttpClient;
import org.apach3.http.client.cache.CacheResponseStatus;
import org.apach3.http.client.methods.HttpGet;
import org.apach3.http.client.methods.HttpPost;
import org.apach3.http.client.methods.HttpPut;
import org.apach3.http.client.methods.HttpRequestBase;
import org.apach3.http.client.utils.URLEncodedUtils;
import org.apach3.http.conn.HttpHostConnectException;
import org.apach3.http.entity.BufferedHttpEntity;
import org.apach3.http.entity.StringEntity;
import org.apach3.http.impl.client.DefaultHttpClient;
import org.apach3.http.impl.client.cache.CacheConfig;
import org.apach3.http.impl.client.cache.CachingHttpClient;
import org.apach3.http.message.BasicNameValuePair;
import org.apach3.http.protocol.BasicHttpContext;
import org.apach3.http.util.EntityUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.apach3.http.client.cache.CacheResponseStatus.CACHE_HIT;
import static org.apach3.http.impl.client.cache.CachingHttpClient.CACHE_RESPONSE_STATUS;

public class HappyDroidService {
  private static final String TAG = HappyDroidService.class.getSimpleName();
  protected static HappyDroidService _instance;
  private static String deviceType;
  private static String deviceOSVersion;
  private static String deviceOSMarketName = "none";

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
  }

  protected HappyDroidService(int fuckYouJava) {
    // leave this for tests.
  }

  public static void setDeviceType(String deviceType) {
    HappyDroidService.deviceType = deviceType;
  }

  public static void setDeviceOSMarketName(String deviceOSMarketName) {
    HappyDroidService.deviceOSMarketName = deviceOSMarketName;
  }

  public static void setDeviceOSVersion(String deviceOSVersion) {
    HappyDroidService.deviceOSVersion = deviceOSVersion;
  }

  public static String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public static String getDeviceOSMarketName() {
    return deviceOSMarketName;
  }

  public static String getDeviceType() {
    return deviceType;
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
          String content = EntityUtils.toString(entity);
          if (HappyDroidConsts.DEBUG) {
            System.out.println("\tResponse: " + content);
          }
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
      if (HappyDroidConsts.DEBUG) {
        System.out.println("PUT " + uri);
      }
      HttpPut request = new HttpPut(uri);
      addDefaultHeaders(request);

      if (objectForServer != null) {
        ObjectMapper mapper = getObjectMapper();
        String apiObjectAsString = mapper.writeValueAsString(objectForServer);
        StringEntity entity = new StringEntity(apiObjectAsString, "UTF-8");
        if (HappyDroidConsts.DEBUG) {
          System.out.println("HTTP ENTITY: " + apiObjectAsString);
        }
        entity.setContentType("application/x-www-form-urlencoded");
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
      if (HappyDroidConsts.DEBUG) {
        System.out.println("Connection failed for: " + uri);
      }
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
        StringEntity entity = new StringEntity(mapper.writeValueAsString(objectForServer), "UTF-8");
        entity.setContentType("application/x-www-form-urlencoded");
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
      throw new RuntimeException(ignored);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void addDefaultHeaders(HttpRequestBase request) {
    request.setHeader("Accept-Charset", "utf-8");
    request.setHeader("Content-Type", "application/json");
  }

  public HttpResponse makeGetRequest(String url, HashMap<String, String> objectForServer) {
    return makeGetRequest(url, objectForServer, false, -1);
  }

  public HttpResponse makeGetRequest(String urlString, HashMap<String, String> queryParams, boolean enableCache, int cacheMaxAge) {
    try {
      URI uri = new URI(urlString);

      if (queryParams != null && queryParams.size() > 0) {
        List<NameValuePair> pairs = Lists.newArrayListWithCapacity(queryParams.size());
        for (String paramName : queryParams.keySet()) {
          pairs.add(new BasicNameValuePair(paramName, queryParams.get(paramName)));
        }

        uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(pairs, "UTF-8"), uri.getFragment());
      }

      HttpGet request = new HttpGet(uri);
      addDefaultHeaders(request);
      Gdx.app.debug(TAG, "REQ: GET " + uri);

      HttpClient client = new DefaultHttpClient();
      if (enableCache) {
        CacheConfig config = new CacheConfig();
        config.setSharedCache(true);
        config.setMaxCacheEntries(256);
        config.setMaxObjectSize(10485760);
        config.setHeuristicCachingEnabled(true);
        config.setHeuristicDefaultLifetime(cacheMaxAge);
        client = new CachingHttpClient(client, new HttpCacheDiskStorage(), config);
      }

      BasicHttpContext context = new BasicHttpContext();
      HttpResponse response = client.execute(request, context);
      CacheResponseStatus responseStatus = (CacheResponseStatus) context.getAttribute(CACHE_RESPONSE_STATUS);

      if (responseStatus == CACHE_HIT) {
        Gdx.app.debug(TAG, "CACHED: GET " + uri + ", " + response.getStatusLine());
      } else {
        Gdx.app.debug(TAG, "RES: GET " + uri + ", " + response.getStatusLine());
      }

      return response;
    } catch (Exception ignored) {
      Gdx.app.error(TAG, "Connection failed for: " + urlString, ignored);
      throw new RuntimeException(ignored);
    }
  }

  public String getDeviceId() {
    return null;
  }


  public boolean isAuthenticated() {
    return false;
  }
}
