package com.unhappyrobot;

import com.unhappyrobot.gamestate.server.TestHttpRequest;
import com.unhappyrobot.http.HttpRequest;
import com.unhappyrobot.tween.TweenSystem;
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
    HttpRequest.setInstance(new TestHttpRequest());
    TweenSystem.getTweenManager();
  }

  protected void afterTestRun() {
    TweenSystem.setTweenManager(null);
  }
}
