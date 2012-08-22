/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.platform.Platform;
import com.happydroids.utils.BackgroundTask;

import java.util.Set;

public class SyncCloudGamesTask extends BackgroundTask {
  protected static final String TAG = SyncCloudGamesTask.class.getSimpleName();

  private static boolean syncing;

  @Override
  protected void execute() throws Exception {
    syncing = true;

    FileHandle storageRoot = GameSaveFactory.getStorageRoot();
    FileHandle[] localSaveFiles = storageRoot.list(".json");

    Gdx.app.log(TAG, "Beginning sync!");
    if (Platform.getConnectionMonitor().isConnectedOrConnecting()) {
      CloudGameSaveCollection cloudGameSaves = new CloudGameSaveCollection();
      cloudGameSaves.fetch();
      Gdx.app.log(TAG, "Fetch complete.");
      syncCloudGameSaves(cloudGameSaves, storageRoot, localSaveFiles);
      Gdx.app.log(TAG, "Sync completed!");
    } else {
      Gdx.app.log(TAG, "Not syncing, no internet connection available.!");
    }
  }

  @Override
  public synchronized void onError(Throwable e) {
    syncing = false;
  }

  @Override
  public synchronized void afterExecute() {
    syncing = false;
  }

  private void syncCloudGameSaves(CloudGameSaveCollection gameSaves, final FileHandle storage, final FileHandle[] files) {
    Set<String> towersProcessed = Sets.newHashSet();
    if (files != null && files.length > 0) {
      for (FileHandle file : files) {
        try {
          GameSave towerData = GameSaveFactory.readMetadata(file.read());
          for (CloudGameSave cloudGameSave : gameSaves.getObjects()) {
            if (towerData.getCloudSaveUri() != null && towerData.getCloudSaveUri()
                                                               .equals(cloudGameSave.getResourceUri())) {
              if (towerData.getFileGeneration() < cloudGameSave.getFileGeneration()) {
                file.writeString(cloudGameSave.getBlob(), false);
              }
            }
          }

          towersProcessed.add(towerData.getCloudSaveUri());
        } catch (Exception ignored) {
        }
      }
    }

    for (CloudGameSave cloudGameSave : gameSaves.getObjects()) {
      if (!towersProcessed.contains(cloudGameSave.getResourceUri())) {
        try {
          Gdx.app.debug(TAG, "Could not find: " + cloudGameSave.getResourceUri() + " on disk!");
          GameSave gameSave = cloudGameSave.getGameSave();
          GameSaveFactory.save(gameSave, storage.child(gameSave.getBaseFilename()));
          storage.child(gameSave.getBaseFilename() + ".png").writeBytes(cloudGameSave.getImage(), false);
        } catch (Exception ignored) {
        }
      }
    }
  }

  public static synchronized boolean isSyncing() {
    return syncing;
  }
}
