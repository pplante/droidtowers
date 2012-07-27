/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tasks;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gui.LoadTowerWindow;
import com.happydroids.utils.BackgroundTask;

public class WaitForCloudSyncTask extends BackgroundTask {
  private LoadTowerWindow loadTowerWindow;

  public WaitForCloudSyncTask(LoadTowerWindow loadTowerWindow) {
    this.loadTowerWindow = loadTowerWindow;
  }

  @Override
  protected void execute() throws Exception {
    while (SyncCloudGamesTask.isSyncing()) {
      Thread.yield();
    }
  }

  @Override
  public synchronized void afterExecute() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        loadTowerWindow.buildGameSaveList();
      }
    });
  }
}
