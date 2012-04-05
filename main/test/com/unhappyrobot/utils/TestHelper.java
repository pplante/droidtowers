/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.utils;

import com.happydroids.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.TestHappyDroidService;

public class TestHelper {
  public static void queueFakeRequest(final String content) {
    ((TestHappyDroidService) HappyDroidService.instance()).queueResponse(content);
  }
}
