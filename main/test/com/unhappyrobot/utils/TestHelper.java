package com.unhappyrobot.utils;

import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.TestHappyDroidService;

public class TestHelper {
  public static void queueFakeRequest(final String content) {
    ((TestHappyDroidService) HappyDroidService.instance()).queueResponse(content);
  }
}
