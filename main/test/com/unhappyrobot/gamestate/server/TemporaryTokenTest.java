package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.NonGLTestRunner;
import com.unhappyrobot.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.Expect.expect;


@RunWith(NonGLTestRunner.class)
public class TemporaryTokenTest {
  @Test
  public void create_shouldReturnATempToken() throws Exception {
    TestHelper.queueFakeRequest("{\"resource_uri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": \"asdfasdfasdfsd\"}, \"value\": 1}");

    TemporaryToken token = TemporaryToken.create();
    expect(token.getValue()).toEqual(1);
    expect(token.getSessionToken()).toEqual("asdfasdfasdfsd");
  }

  @Test
  public void hasSessionToken_shouldReturnFalse_whenTokenIsNull() throws Exception {
    TestHelper.queueFakeRequest("{\"session\": {\"token\": null}, \"value\": 1}");

    TemporaryToken token = TemporaryToken.create();
    expect(token.getValue()).toEqual(1);
    expect(token.getSessionToken()).toBeNull();

    expect(token.hasSessionToken()).toBeFalse();
  }

  @Test
  public void validate_shouldRequestTokenUpdates() {
    TestHelper.queueFakeRequest("{\"resource_uri\": \"/api/v1/temporarytoken/1/\", \"session\": null, \"value\": 1}");

    TemporaryToken token = TemporaryToken.create();
    expect(token.hasSessionToken()).toBeFalse();

    TestHelper.queueFakeRequest("{\"resource_uri\": \"/api/v1/temporarytoken/1/\", \"session\": null, \"value\": 1}");

    expect(token.validate()).toBeFalse();

    TestHelper.queueFakeRequest("{\"resource_uri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": \"asdfasdfasdfsd\"}, \"value\": 1}");
    expect(token.validate()).toBeTrue();
  }
}
