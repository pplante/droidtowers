/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.platform.HappyDroidsDesktopUncaughtExceptionHandler;

import java.io.File;
import java.io.IOException;

public class GameUpdateCheckWorker extends SimpleSwingWorker {
  private final GameUpdateDownloader updateDownloader;

  public GameUpdateCheckWorker(File gameStorage, File gameJar) {
    this.updateDownloader = new GameUpdateDownloader(gameStorage, gameJar);

    Thread.currentThread().setUncaughtExceptionHandler(new HappyDroidsDesktopUncaughtExceptionHandler());
  }


  @Override
  protected Void doInBackground() {
    try {
      updateDownloader.checkForUpdates();
      firePropertyChange("updateCheckComplete", false, updateDownloader.updateAvailable());

      if (updateDownloader.updateAvailable()) {
        updateDownloader.fetchUpdates();
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      firePropertyChange("updateProcessComplete", 0, 1);
    }

    return null;
  }

  @Override
  protected void done() {
  }
}
