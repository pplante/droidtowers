/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.droidtowers.gamestate.server.RunnableQueue;

public abstract class PlatformConnectionMonitor {
  private RunnableQueue postConnectRunnables;

  public abstract boolean isConnectedOrConnecting();

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
}
