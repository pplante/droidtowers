/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.server.HappyDroidService;
import com.happydroids.server.TestHappyDroidService;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class SparkyTestRunner extends BlockJUnit4ClassRunner {
  public SparkyTestRunner(Class<?> klass) throws InitializationError {
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
    HappyDroidService.setInstance(new TestHappyDroidService());
  }

  protected void afterTestRun() {
  }
}
