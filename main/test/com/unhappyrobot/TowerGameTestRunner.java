package com.unhappyrobot;

import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.TestHappyDroidService;
import com.unhappyrobot.tween.TweenSystem;
import org.junit.runners.model.InitializationError;

public class TowerGameTestRunner extends GdxTestRunner {
  public TowerGameTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected void beforeTestRun() {
    HappyDroidService.setInstance(new TestHappyDroidService());
    TweenSystem.getTweenManager();
  }

  @Override
  protected void afterTestRun() {
    TweenSystem.setTweenManager(null);
  }
}
