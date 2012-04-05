/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.google.common.collect.Lists;
import com.happydroids.server.HappyDroidService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicStatusLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHappyDroidService extends HappyDroidService {
  private LinkedList<String> responses;

  public TestHappyDroidService() {
    super();
    responses = Lists.newLinkedList();
  }

  @Override
  public HttpResponse makeGetRequest(String uri) {
    try {
      String responseString = responses.poll();
      byte[] bytes = responseString.getBytes();

      HttpEntity entity = mock(HttpEntity.class);
      when(entity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
      when(entity.getContentLength()).thenReturn((long) bytes.length);
      HttpResponse response = mock(HttpResponse.class);
      when(response.getEntity()).thenReturn(entity);
      when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "OK"));
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public HttpResponse makePostRequest(String uri, Object forServerDoNotCare) {
    try {
      String responseString = responses.poll();
      byte[] bytes = responseString.getBytes();

      HttpEntity entity = mock(HttpEntity.class);
      when(entity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
      when(entity.getContentLength()).thenReturn((long) bytes.length);
      HttpResponse response = mock(HttpResponse.class);
      when(response.getEntity()).thenReturn(entity);
      when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 201, "OK"));
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void queueResponse(String content) {
    responses.add(content);
  }

  @Override
  public void withNetworkConnection(Runnable runnable) {
    runnable.run();
  }
}
