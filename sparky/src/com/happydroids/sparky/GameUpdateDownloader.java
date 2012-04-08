/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.google.common.collect.Iterables;
import com.happydroids.HappyDroidConsts;
import com.happydroids.server.GameUpdate;
import com.happydroids.server.HappyDroidService;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class GameUpdateDownloader {
  private final GameUpdateChecker updateChecker;
  private Runnable postDownloadRunnable;
  private final File gameStorage;
  private final File gameJar;

  public GameUpdateDownloader(File gameStorage, File gameJar) {
    this.gameStorage = gameStorage;
    this.gameJar = gameJar;
    updateChecker = new GameUpdateChecker(gameStorage, this.gameJar);
  }

  public void fetchUpdates() throws IOException {
    if (updateChecker.hasCurrentVersion()) {
      if (postDownloadRunnable != null) {
        postDownloadRunnable.run();
      }
      return;
    }

    Logger.getAnonymousLogger().info(String.format("Downloading %d updates!", updateChecker.getPendingUpdates().size()));

    final JarJoiner joiner = new JarJoiner(gameJar);
    for (GameUpdate gameUpdate : updateChecker.getPendingUpdates()) {
      Logger.getAnonymousLogger().info("Downloading: " + gameUpdate.gitSha);
      File updateFile = fetchGameUpdate(gameUpdate, updateChecker.shouldUsePatches());
      if (updateFile == null) {
        throw new GameFileDownloadException("A critical file failed to download.");
      }

      joiner.addFile(updateFile);
    }

    GameUpdate latestUpdate = Iterables.getLast(updateChecker.getPendingUpdates(), null);
    if (latestUpdate != null) {
      joiner.join(latestUpdate.version, latestUpdate.gitSha);

      if (postDownloadRunnable != null) {
        postDownloadRunnable.run();
      }
    }
  }

  private File fetchGameUpdate(GameUpdate gameUpdate, boolean shouldUsePatch) throws IOException {
    String contentUrl = shouldUsePatch ? gameUpdate.patchFile.content : gameUpdate.fullRelease.content;

    HttpResponse httpResponse = HappyDroidService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + contentUrl);

    if (httpResponse == null || httpResponse.getStatusLine() == null || httpResponse.getStatusLine().getStatusCode() != 200) {
      return null;
    }

    File updateFile = File.createTempFile("gameUpdate", ".jar");
    FileOutputStream fileOutputStream = new FileOutputStream(updateFile);
    new BufferedHttpEntity(httpResponse.getEntity()).writeTo(fileOutputStream);
    fileOutputStream.flush();
    fileOutputStream.close();

    return updateFile;
  }

  public void setPostDownloadRunnable(Runnable postDownloadRunnable) {
    this.postDownloadRunnable = postDownloadRunnable;
  }

  public void checkForUpdates() throws IOException {
    updateChecker.parseLocalGameJar();
    updateChecker.fetchUpdates();
    updateChecker.selectUpdates();
  }

  public boolean updateAvailable() {
    return !updateChecker.hasCurrentVersion() || (updateChecker.getPendingUpdates() != null && !updateChecker.getPendingUpdates().isEmpty());
  }

  private class GameFileDownloadException extends IOException {
    public GameFileDownloadException(String message) {
      super(message);
    }
  }
}
