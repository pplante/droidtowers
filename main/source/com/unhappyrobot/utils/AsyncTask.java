package com.unhappyrobot.utils;

public abstract class AsyncTask {
  protected Thread thread;

  public AsyncTask() {
    thread = new Thread() {
      @Override
      public void run() {
        beforeExecute();
        execute();
        afterExecute();
      }
    };

    thread.setPriority(Thread.MIN_PRIORITY);
    thread.setDaemon(true);
  }

  public synchronized void beforeExecute() {
  }

  public abstract void execute();

  public synchronized void afterExecute() {
  }

  public final void run() {
    thread.start();
  }
}
