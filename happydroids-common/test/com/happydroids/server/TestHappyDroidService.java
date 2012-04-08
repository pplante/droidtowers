/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.google.common.collect.Maps;
import com.happydroids.HappyDroidConsts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class TestHappyDroidService extends HappyDroidService {
  private HashMap<String, LinkedList<byte[]>> knownUris;

  public TestHappyDroidService() {
    super(13);
    knownUris = Maps.newHashMap();
    hasNetworkConnection = true;
  }

  @Override
  public HttpResponse makeGetRequest(String uri) {
    try {
      byte[] bytes = getNextQueuedResponse(uri);

      HttpEntity entity = Mockito.mock(HttpEntity.class);
      Mockito.when(entity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
      Mockito.when(entity.getContentLength()).thenReturn((long) bytes.length);
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getEntity()).thenReturn(entity);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "OK"));
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private byte[] getNextQueuedResponse(String uri) {
    if (!knownUris.containsKey(uri) || knownUris.get(uri).isEmpty()) {
      throw new RuntimeException("There is no request queued up for: " + uri);
    }

    return knownUris.get(uri).pop();
  }

  @Override
  public HttpResponse makePostRequest(String uri, Object forServerDoNotCare) {
    try {
      byte[] bytes = getNextQueuedResponse(uri);

      HttpEntity entity = Mockito.mock(HttpEntity.class);
      Mockito.when(entity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
      Mockito.when(entity.getContentLength()).thenReturn((long) bytes.length);
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getEntity()).thenReturn(entity);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 201, "OK"));
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void queueResponse(String uri, String content) {
    queueResponse(uri, content.getBytes());
  }

  public void queueResponse(String uri, byte[] stream) {
    if (!uri.contains("http://") && !uri.contains("https://")) {
      uri = HappyDroidConsts.HAPPYDROIDS_URI + uri;
    }

    if (!knownUris.containsKey(uri)) {
      knownUris.put(uri, new LinkedList<byte[]>());
    }

    knownUris.get(uri).push(stream);
  }

  public static void disableNetworkConnection() {
    instance().hasNetworkConnection = false;
  }

  public HashMap<String, LinkedList<byte[]>> getResponseQueue() {
    return knownUris;
  }

  @Override
  public void withNetworkConnection(Runnable runnable) {
    if (hasNetworkConnection) {
      runnable.run();
    }
  }
}
