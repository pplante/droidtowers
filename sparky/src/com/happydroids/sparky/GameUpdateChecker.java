/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.google.common.collect.Lists;
import com.happydroids.platform.Platform;
import com.happydroids.server.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class GameUpdateChecker {
  private HappyDroidServiceCollection<GameUpdate> updates;
  private final File gameStorage;
  private final File gameJar;
  private boolean usePatches;
  private boolean alreadyAtCurrentVersion;
  private String localGameJarVersionSHA;
  private List<GameUpdate> pendingUpdates;

  public GameUpdateChecker(File gameStorage, File gameJar) {
    this.gameStorage = gameStorage;
    this.gameJar = gameJar;
    pendingUpdates = Lists.newArrayList();
    alreadyAtCurrentVersion = false;
    usePatches = false;
    localGameJarVersionSHA = null;
  }

  public void parseLocalGameJar() {
    if (gameJar.exists()) {
      try {
        JarFile jarFile = new JarFile(gameJar);
        if (jarFile != null) {
          if (jarFile.getManifest() != null) {
            Attributes mainAttributes = jarFile.getManifest().getMainAttributes();
            if (mainAttributes != null) {
              localGameJarVersionSHA = mainAttributes.getValue("Game-Version-SHA");
            }
          }
        }
      } catch (IOException ignored) {
      }
    }
  }

  public void fetchUpdates() {
    updates = new GameUpdateCollection();
    updates.fetchBlocking(new ApiCollectionRunnable<HappyDroidServiceCollection<GameUpdate>>());
  }

  public HappyDroidServiceCollection<GameUpdate> getUpdates() {
    return updates;
  }

  public void selectUpdates() throws IOException {
    if (localGameJarVersionSHA != null) {
      if (!updates.isEmpty()) {
        GameUpdate latestUpdate = updates.getObjects().get(0);
        if (latestUpdate.gitSha.equals(localGameJarVersionSHA)) {
          alreadyAtCurrentVersion = true;
          return;
        } else {
          findUpdatesSinceLastValidSha();
        }

        if (!usePatches) {
          pendingUpdates.add(latestUpdate);
        }
      }
    } else if (!updates.isEmpty()) {
      pendingUpdates.add(updates.getObjects().get(0));
    }
  }

  private void findUpdatesSinceLastValidSha() {
    if (localGameJarVersionSHA == null) {
      return;
    }

    for (GameUpdate update : updates.getObjects()) {
      if (update.getGitSHA().equals(localGameJarVersionSHA)) {
        usePatches = true;
        break;
      } else if (update.patchFile != null) {
        pendingUpdates.add(update);
      } else {
        usePatches = false;
        pendingUpdates.clear();
        break;
      }
    }
  }

  public boolean shouldUsePatches() {
    return usePatches;
  }

  public List<GameUpdate> getPendingUpdates() {
    return Lists.reverse(pendingUpdates);
  }

  public boolean hasCurrentVersion() {
    if (!Platform.getConnectionMonitor().isConnectedOrConnecting()) {
      return localGameJarVersionSHA != null;
    }

    return alreadyAtCurrentVersion;
  }

  public String getLocalGameJarVersionSHA() {
    return localGameJarVersionSHA;
  }
}
