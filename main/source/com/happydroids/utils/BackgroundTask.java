/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.utils;

import com.happydroids.error.ErrorUtil;
import com.happydroids.platform.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class BackgroundTask {
  protected static final String TAG = BackgroundTask.class.getSimpleName();
  protected static ExecutorService threadPool;

  protected Thread thread;
  private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  private static PostExecuteManager postExecuteManager;
  private boolean canceled;


  public BackgroundTask() {

  }

  public static void setPostExecuteManager(PostExecuteManager postExecuteManager) {
    BackgroundTask.postExecuteManager = postExecuteManager;
  }

  public synchronized void beforeExecute() {
  }

  protected abstract void execute() throws Exception;

  public synchronized void afterExecute() {
  }

  public synchronized void onError(Throwable e) {
    ErrorUtil.rethrowError(e);
  }

  public final void run() {
    if (threadPool == null) {
      threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
        public Thread newThread(Runnable r) {
          Thread thread = new Thread(r, "BackgroundTaskThread");
          thread.setUncaughtExceptionHandler(Platform.getUncaughtExceptionHandler());
          thread.setPriority(Thread.MIN_PRIORITY);
          thread.setDaemon(true);
          return thread;
        }
      });
    }

    threadPool.submit(new Runnable() {
      public void run() {
        try {
          if (!canceled) {
            beforeExecute();
          }
          if (!canceled) {
            execute();
          }
          if (!canceled) {
            postExecuteManager.postRunnable(new Runnable() {
              public void run() {
                afterExecute();
              }
            });
          }
        } catch (final Throwable e) {
          postExecuteManager.postRunnable(new Runnable() {
            public void run() {
              onError(e);
            }
          });
        }
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

  public void cancel() {
    canceled = true;
  }

  public interface PostExecuteManager {
    public void postRunnable(Runnable runnable);
  }
}
