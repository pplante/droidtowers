package com.unhappyrobot.utils;

import com.unhappyrobot.gamestate.server.TestHttpRequest;
import com.unhappyrobot.http.HttpRequest;

public class TestHelper {
  public static void queueFakeRequest(final String content) {
    ((TestHttpRequest) HttpRequest.instance()).queueResponse(content);
  }
}
