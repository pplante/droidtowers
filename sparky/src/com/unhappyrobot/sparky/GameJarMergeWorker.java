/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.sparky;

import javax.swing.*;
import java.io.File;

public class GameJarMergeWorker extends SwingWorker<Void, Void> {
  private final File mergedJarFile;
  private final String gameVersion;

  public GameJarMergeWorker(File mergedJarFile, String gameVersion) {
    this.mergedJarFile = mergedJarFile;
    this.gameVersion = gameVersion;
  }

  @Override
  protected Void doInBackground() {
    try {
      final JarJoiner joiner = new JarJoiner(mergedJarFile);
      joiner.addFile(new File("tmp/patch.jar"));
      joiner.addFile(new File("tmp/DroidTowers-release.jar"));
      joiner.setProgressCallback(new Runnable() {
        public void run() {
          setProgress((int) ((float) joiner.getNumEntriesProcessed() / (float) joiner.getNumTotalEntries() * 100.0));
          firePropertyChange("progress", 0, getProgress());
        }
      });

      joiner.join(gameVersion);
    } catch (Exception e) {
      e.printStackTrace();
      cancel(true);
    }

    return null;
  }

  @Override
  protected void done() {
    firePropertyChange("done", 0, 1);
  }
}
