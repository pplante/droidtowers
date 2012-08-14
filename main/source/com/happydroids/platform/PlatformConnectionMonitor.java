/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apach3.http.HttpResponse;

public class PlatformConnectionMonitor {
  protected RunnableQueue postConnectRunnables;
  private boolean networkState;


  public PlatformConnectionMonitor() {
    postConnectRunnables = new RunnableQueue();
    monitorThread.start();
  }

  public boolean isConnectedOrConnecting() {
    return networkState;
  }

  public void withConnection(Runnable runnable) {
    if (postConnectRunnables == null) {
      postConnectRunnables = new RunnableQueue();
    }

    if (isConnectedOrConnecting()) {
      runnable.run();
    } else {
      postConnectRunnables.push(runnable);
    }
  }

  protected void runAllPostConnectRunnables() {
    postConnectRunnables.runAll();
  }

  public void dispose() {
    monitorThread = null;
  }

  @SuppressWarnings("FieldCanBeLocal")
  protected Thread monitorThread = new Thread(PlatformConnectionMonitor.class.getSimpleName()) {
    @Override
    public void run() {
      while (monitorThread == Thread.currentThread()) {
        try {
          HttpResponse response = TowerGameService.instance()
                                          .makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + "/ping", null, false, -1);
          if (response != null) {
            networkState = response.getStatusLine().getStatusCode() == 200;

            if (networkState) {
              runAllPostConnectRunnables();
            }
          }
        } catch (Throwable ignored) {
          networkState = false;
        } finally {
          try {
            Thread.sleep(HappyDroidConsts.HAPPYDROIDS_PING_FREQUENCY);
          } catch (InterruptedException ignored) {
            monitorThread = null;
          }
        }
      }
    }
  };
}
