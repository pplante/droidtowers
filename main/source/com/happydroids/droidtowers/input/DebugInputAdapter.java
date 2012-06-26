/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gui.DebugWindow;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.PNG;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DebugInputAdapter extends InputAdapter {
  public StringBuilder keys = new StringBuilder();

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case InputSystem.Keys.F1:
        TowerAssetManager.assetManager().invalidateAllTextures();
        return true;

      case Input.Keys.F2:
        SceneManager.restartActiveScene();
        return true;

      case Input.Keys.F6:
        takeScreenShot();
        return true;

      case Input.Keys.ALT_RIGHT:
      case Input.Keys.MENU:
        new DebugWindow(TowerGame.getRootUiStage()).show();
        return true;
    }

    return false;
  }

  private void takeScreenShot() {
    Pixmap pixmap = getScreenShot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    try {
      FileHandle screenShotFile = Gdx.files.external("Desktop/DroidTowers_" + System.currentTimeMillis() + ".png");
      screenShotFile.writeBytes(PNG.toPNG(pixmap), false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Pixmap getScreenShot(int x, int y, int w, int h, boolean flipY) {
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

  @Override
  public boolean keyTyped(char character) {
    keys.append(character);

    if (SceneManager.getActiveScene() instanceof TowerScene) {
      if (keys.toString().endsWith("ggmoney")) {
        Player.instance().addCurrency(100000);
        HeadsUpDisplay.showToast("Money!!!!");
      }
    }

    if (keys.length() == 20) {
      keys.deleteCharAt(0);
    }

    return false;
  }
}
