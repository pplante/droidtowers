/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.tween.TweenSystem;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class NonGLTestRunner extends BlockJUnit4ClassRunner {
  public NonGLTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Statement methodBlock(FrameworkMethod method) {
    beforeTestRun();

    final Statement statement = super.methodBlock(method);
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          statement.evaluate();
        } finally {
          afterTestRun();
        }
      }
    };
  }

  protected void beforeTestRun() {
    TweenSystem.getTweenManager();
    Gdx.files = new TestGdxFiles();
    Gdx.app = new TestGdxApplication();
  }

  protected void afterTestRun() {
    TweenSystem.setTweenManager(null);
  }
}
