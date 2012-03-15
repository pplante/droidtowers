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
    TestHelper.queueFakeRequest("{\"resource_uri\": \"/api/v1/temporarytoken/1/\", \"session\": {\"token\": \"asdfasdfasdfsd\"}, \"value\": 86672}");

    TemporaryToken token = TemporaryToken.create();
    expect(token.getValue()).toEqual(86672L);
    expect(token.getSessionToken()).toEqual("asdfasdfasdfsd");
  }

  @Test
  public void hasSessionToken_shouldReturnFalse_whenTokenIsNull() throws Exception {
    TestHelper.queueFakeRequest("{\"session\": {\"token\": null}, \"value\": 86672}");

    TemporaryToken token = TemporaryToken.create();
    expect(token.getValue()).toEqual(86672L);
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
