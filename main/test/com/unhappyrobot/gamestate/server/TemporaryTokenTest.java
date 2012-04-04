/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerGameTestRunner;
import com.unhappyrobot.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.Expect.expect;


@RunWith(TowerGameTestRunner.class)
public class TemporaryTokenTest {
  @Test
  public void create_shouldReturnATempToken() throws Exception {
    TestHelper.queueFakeRequest("{\"resourceUri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": \"MYSESSIONTOKEN\"}, \"value\": \"TOK3N\"}");

    TemporaryToken token = new TemporaryToken();
    token.save();
    expect(token.getValue()).toEqual("TOK3N");
    expect(token.getSessionToken()).toEqual("MYSESSIONTOKEN");
  }

  @Test
  public void hasSessionToken_shouldReturnFalse_whenTokenIsNull() throws Exception {
    TestHelper.queueFakeRequest("{\"resourceUri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": null}, \"value\": \"TOK3N\"}");

    TemporaryToken token = new TemporaryToken();
    token.save();
    expect(token.getValue()).toEqual("TOK3N");
    expect(token.getSessionToken()).toBeNull();

    expect(token.hasSessionToken()).toBeFalse();
  }

  @Test
  public void validate_shouldRequestTokenUpdates() {
    TestHelper.queueFakeRequest("{\"resourceUri\": \"/api/v1/temporarytoken/1/\", \"session\": null, \"value\": \"TOK123\"}");

    TemporaryToken token = new TemporaryToken();
    token.save();
    expect(token.getResourceUri()).toEqual("/api/v1/temporarytoken/1/");
    expect(token.hasSessionToken()).toBeFalse();

    TestHelper.queueFakeRequest("{\"resourceUri\": \"/api/v1/temporarytoken/1/\", \"session\": null, \"value\": \"TOK123\"}");
    expect(token.getResourceUri()).toEqual("/api/v1/temporarytoken/1/");
    expect(token.validate()).toBeFalse();

    TestHelper.queueFakeRequest("{\"resourceUri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": \"asdfasdfasdfsd\"}, \"value\": \"TOK123\"}");
    expect(token.validate()).toBeTrue();
  }
}
