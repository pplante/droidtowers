/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.sparky.HappyDroidTestRunner;
import org.junit.runners.model.InitializationError;

public class NonGLTestRunner extends HappyDroidTestRunner {
  public NonGLTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  protected void beforeTestRun() {
    super.beforeTestRun();


    Gdx.files = new TestGdxFiles();
    Gdx.app = new TestGdxApplication(null, null);

    TweenSystem.getTweenManager();
    TowerGameService.setInstance(new TestTowerGameService());
  }

  protected void afterTestRun() {
    super.afterTestRun();

    TweenSystem.setTweenManager(null);

    Gdx.app = null;
    Gdx.graphics = null;
    Gdx.audio = null;
    Gdx.files = null;
    Gdx.input = null;
  }

}
