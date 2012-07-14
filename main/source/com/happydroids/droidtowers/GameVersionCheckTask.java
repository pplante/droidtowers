/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gui.GameUpdateDialog;
import com.happydroids.server.GameUpdate;
import com.happydroids.server.GameUpdateCollection;
import com.happydroids.utils.BackgroundTask;

class GameVersionCheckTask extends BackgroundTask {
  private boolean foundNewerVersion;
  private GameUpdateCollection updates;
  private GameUpdate latestUpdate;

  @Override
  protected void execute() throws Exception {
    updates = new GameUpdateCollection();
    updates.fetch();

    latestUpdate = null;
    for (GameUpdate update : updates.getObjects()) {
      if (update.versionCode > HappyDroidConsts.VERSION_CODE) {
        foundNewerVersion = true;

        if (latestUpdate == null || latestUpdate.versionCode < update.versionCode) {
          latestUpdate = update;
        }
        break;
      }
    }

    if (foundNewerVersion) {
      while (!TowerAssetManager.preloadFinished()) {
        try {
          Thread.sleep(500);
          Thread.yield();
        } catch (InterruptedException ignored) {

        }
      }
    }
  }

  @Override
  public synchronized void afterExecute() {
    if (foundNewerVersion) {
      new GameUpdateDialog(latestUpdate).show();
    }
  }
}
