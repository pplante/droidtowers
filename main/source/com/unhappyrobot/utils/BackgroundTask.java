/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.utils;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.TowerGame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class BackgroundTask {
  private static final String TAG = BackgroundTask.class.getSimpleName();
  protected static ExecutorService threadPool;

  protected Thread thread;

  static {
    threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "BackgroundTaskThread");
        thread.setUncaughtExceptionHandler(TowerGame.getUncaughtExceptionHandler());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        return thread;
      }
    });
  }

  public BackgroundTask() {

  }

  public synchronized void beforeExecute() {
  }

  public abstract void execute();

  public synchronized void afterExecute() {
  }

  public final void run() {
    threadPool.submit(new Runnable() {
      public void run() {
        beforeExecute();
        execute();
        afterExecute();
      }
    });
  }

  public static void dispose() {
    if (threadPool != null) {
      threadPool.shutdown();
      Gdx.app.debug(TAG, "Shutting down background tasks...");
      try {
        threadPool.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {
      }
    }
  }
}
