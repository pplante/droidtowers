package com.unhappyrobot;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.Map;

public class GdxTestRunner extends BlockJUnit4ClassRunner implements ApplicationListener {

  private Map<FrameworkMethod, RunNotifier> invokeInRender = new HashMap<FrameworkMethod, RunNotifier>();

  public GdxTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
    LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
    conf.width = 800;
    conf.height = 600;
    conf.title = "Gdx Test Runner";
    new LwjglApplication(this, conf);
  }

  public void create() {
  }

  public void resume() {
  }

  public void render() {
    synchronized (invokeInRender) {
      for (Map.Entry<FrameworkMethod, RunNotifier> each : invokeInRender.entrySet()) {
        super.runChild(each.getKey(), each.getValue());
      }
      invokeInRender.clear();
    }
  }

  public void resize(int width, int height) {
  }

  public void pause() {
  }

  public void dispose() {
  }

  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    synchronized (invokeInRender) {
      //add for invoking in render phase, where gl context is available
      invokeInRender.put(method, notifier);
    }
    //wait until that test was invoked
    waitUntilInvokedInRenderMethod();
  }

  /**
   *
   */
  private void waitUntilInvokedInRenderMethod() {
    try {
      while (true) {
        Thread.sleep(10);
        synchronized (invokeInRender) {
          if (invokeInRender.isEmpty()) break;
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
