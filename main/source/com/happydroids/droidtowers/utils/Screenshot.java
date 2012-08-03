/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.happydroids.droidtowers.platform.Display;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.badlogic.gdx.Application.ApplicationType.Android;

public class Screenshot {
  public static void capture() {
    Pixmap pixmap = captureFromBuffer(0, 0, Display.getWidth(), Display.getHeight(), true);

    try {
      String fileName = "DroidTowers_" + System.currentTimeMillis() + ".png";
      FileHandle storagePath = Gdx.files.external(Gdx.app.getType().equals(Android) ? "" : "Desktop/");
      FileHandle screenShotFile = storagePath.child(fileName);
      screenShotFile.writeBytes(PNG.toPNG(pixmap), false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static Pixmap captureFromBuffer(int x, int y, int w, int h, boolean flipY) {
    Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);

    final Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
    ByteBuffer pixels = pixmap.getPixels();
    Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

    final int numBytes = w * h * 4;
    byte[] lines = new byte[numBytes];
    if (flipY) {
      final int numBytesPerLine = w * 4;
      for (int i = 0; i < h; i++) {
        pixels.position((h - i - 1) * numBytesPerLine);
        pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
      }
      pixels.clear();
      pixels.put(lines);
    } else {
      pixels.clear();
      pixels.get(lines);
    }

    return pixmap;
  }
}
