/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

import com.google.common.collect.Maps;
import org.apach3.http.Header;
import org.apach3.http.HttpEntity;
import org.apach3.http.HttpResponse;
import org.apach3.http.ProtocolVersion;
import org.apach3.http.message.BasicStatusLine;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import static org.mockito.Matchers.anyString;

public class HttpTestHelper {
  private static HttpTestHelper inst;
  public HashMap<String, LinkedList<byte[]>> knownUris;
  private HashMap<String, LinkedList<Header[]>> knownHeaders;

  public static HttpTestHelper instance() {
    if (inst == null) {
      inst = new HttpTestHelper();
    }

    return inst;
  }

  private HttpTestHelper() {
    knownUris = Maps.newHashMap();
    knownHeaders = Maps.newHashMap();
  }

  public HttpResponse makeGetRequest(String uri) {
    return makeRequest(uri, 200, "OK");
  }

  public HttpResponse makePostRequest(String uri, Object ignored) {
    return makeRequest(uri, 201, "OK");
  }

  private HttpResponse makeRequest(String uri, int statusCode, String reasonPhrase) {
    try {
      byte[] bytes = getNextQueuedResponse(uri);
      final Header[] headers = getNextQueuedResponseHeaders(uri);

      HttpEntity entity = Mockito.mock(HttpEntity.class);
      Mockito.when(entity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
      Mockito.when(entity.getContentLength()).thenReturn((long) bytes.length);
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getEntity()).thenReturn(entity);
      Mockito.when(response.getStatusLine())
              .thenReturn(new BasicStatusLine(new ProtocolVersion("http", 1, 1), statusCode, reasonPhrase));
      Mockito.when(response.getHeaders(anyString())).thenAnswer(new Answer<Header[]>() {
        public Header[] answer(InvocationOnMock invocation) throws Throwable {
          Object[] arguments = invocation.getArguments();
          String headerName = (String) arguments[0];

          for (Header header : headers) {
            if (header.getName().equals(headerName)) {
              return new Header[]{header};
            }
          }

          return null;
        }
      });
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public byte[] getNextQueuedResponse(String uri) {
    if (!knownUris.containsKey(uri) || knownUris.get(uri).isEmpty()) {
      throw new RuntimeException("There is no request queued up for: " + uri);
    }

    return knownUris.get(uri).pop();
  }

  public Header[] getNextQueuedResponseHeaders(String uri) {
    if (!knownHeaders.containsKey(uri) || knownHeaders.get(uri).isEmpty()) {
      throw new RuntimeException("There is no request queued up for: " + uri);
    }

    return knownHeaders.get(uri).pop();
  }

  public void queueResponse(String uri, String content) {
    queueResponse(uri, content.getBytes(), null);
  }

  public void queueResponse(String uri, byte[] stream, Header[] headers) {
    if (!uri.contains("http://") && !uri.contains("https://")) {
      uri = HappyDroidConsts.HAPPYDROIDS_URI + uri;
    }

    if (!knownUris.containsKey(uri)) {
      knownUris.put(uri, new LinkedList<byte[]>());
    }

    if (!knownHeaders.containsKey(uri)) {
      knownHeaders.put(uri, new LinkedList<Header[]>());
    }

    knownUris.get(uri).push(stream);
    knownHeaders.get(uri).push(headers);
  }

  public HashMap<String, LinkedList<byte[]>> getResponseQueue() {
    return knownUris;
  }

  public static void resetInstance() {
    inst = null;
  }
}
