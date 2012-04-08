/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.google.common.collect.Lists;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class TestHappyDroidService extends HappyDroidService {
  private LinkedList<String> responses;

  public TestHappyDroidService() {
    super();
    responses = Lists.newLinkedList();
    hasNetworkConnection = true;
  }

  @Override
  public HttpResponse makeGetRequest(String uri) {
    try {
      byte[] bytes = getNextQueuedResponse();

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

  private byte[] getNextQueuedResponse() {
    byte[] bytes = null;
    String responseString = responses.poll();
    if (responseString != null) {
      bytes = responseString.getBytes();
    }
    return bytes;
  }

  @Override
  public HttpResponse makePostRequest(String uri, Object forServerDoNotCare) {
    try {
      byte[] bytes = getNextQueuedResponse();

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

  public void queueResponse(String content) {
    responses.add(content);
  }

  public static void disableNetworkConnection() {
    instance().hasNetworkConnection = false;
  }
}
