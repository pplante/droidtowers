package com.unhappyrobot.gamestate.server;

import com.google.common.collect.Lists;
import com.unhappyrobot.http.HttpRequest;
import com.unhappyrobot.http.HttpResponse;

import java.io.IOException;
import java.util.LinkedList;

public class TestHttpRequest extends HttpRequest {
  private LinkedList<String> responses;

  public TestHttpRequest() {
    responses = Lists.newLinkedList();
  }

  @Override
  protected HttpResponse getRequest(String urlString) throws IOException {
    return new HttpResponse(responses.poll());
  }

  public void queueResponse(String content) {
    responses.add(content);
  }
}
