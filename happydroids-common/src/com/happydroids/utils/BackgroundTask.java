/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class BackgroundTask {
  private static final String TAG = BackgroundTask.class.getSimpleName();
  protected static ExecutorService threadPool;

  protected Thread thread;
  private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  private static PostExecuteManager postExecuteManager;


  public BackgroundTask() {

  }

  public static void setPostExecuteManager(PostExecuteManager postExecuteManager) {
    BackgroundTask.postExecuteManager = postExecuteManager;
  }

  public synchronized void beforeExecute() {
  }

  protected abstract void execute();

  public synchronized void afterExecute() {
  }

  public final void run() {
    if (threadPool == null) {
      threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
          Thread thread = new Thread(r, "BackgroundTaskThread");
          if (uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
          }
          thread.setPriority(Thread.MIN_PRIORITY);
          thread.setDaemon(true);
          return thread;
        }
      });
    }

    threadPool.submit(new Runnable() {
      public void run() {
        beforeExecute();
        execute();
        postExecuteManager.postRunnable(new Runnable() {
          public void run() {
            afterExecute();
          }
        });
      }
    });
  }

  public static void dispose() {
    if (threadPool != null) {
      threadPool.shutdown();
      Logger.getLogger(TAG).info("Shutting down background tasks...");
      try {
        threadPool.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {
      } finally {
        threadPool = null;
      }
    }
  }

  public static void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    BackgroundTask.uncaughtExceptionHandler = uncaughtExceptionHandler;
  }

  public interface PostExecuteManager {
    public void postRunnable(Runnable runnable);
  }
}
