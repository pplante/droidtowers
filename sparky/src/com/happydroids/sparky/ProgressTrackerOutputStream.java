/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProgressTrackerOutputStream extends OutputStream {
  private final FileOutputStream fileOutputStream;
  private int currentPosition;
  private Runnable progressListener;

  public ProgressTrackerOutputStream(FileOutputStream fileOutputStream) {
    this.fileOutputStream = fileOutputStream;
  }


  @Override
  public void write(int i) throws IOException {
  }

  @Override
  public void write(byte[] bytes) throws IOException {

    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    do {
      byte[] chunk = new byte[10240];
      int read = inputStream.read(chunk);
      fileOutputStream.write(chunk, 0, read);
      fileOutputStream.flush();
      currentPosition += read;

      if (progressListener != null) {
        progressListener.run();
      }
    } while (inputStream.available() > 0);
  }

  public int getProgress() {
    return currentPosition;
  }

  public void setProgressListener(Runnable progressListener) {
    this.progressListener = progressListener;
  }

  @Override
  public void close() throws IOException {
    fileOutputStream.close();
  }
}
