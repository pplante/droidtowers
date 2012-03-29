package com.unhappyrobot.utils;

import com.unhappyrobot.TowerGame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class BackgroundTask {
  protected Thread thread;
  protected static ExecutorService threadPool;

  static {
    threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
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
    }
  }
}
