/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.platform.Display;

import java.util.Map;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapNearestNearest;

public class TowerAssetManager {
  private static final String TAG = TowerAssetManager.class.getSimpleName();
  private static MemoryTrackingAssetManager assetManager;
  public static final String WHITE_SWATCH = "swatches/swatch-white.png";
  private static Skin skin;

  @SuppressWarnings("unchecked")
  public static MemoryTrackingAssetManager assetManager() {
    if (assetManager == null) {
      assetManager = new MemoryTrackingAssetManager();
      if (HappyDroidConsts.DEBUG) {
        assetManager.getLogger().setLevel(Logger.ERROR);
      }

      Texture.setAssetManager(assetManager);

      for (Map.Entry<String, Class> entry : TowerAssetManagerFilesList.preloadFiles.entrySet()) {
        assetManager.load(checkForHDPI(entry.getKey()), entry.getValue());
      }

      for (Map.Entry<String, Class> entry : TowerAssetManagerFilesList.files.entrySet()) {
        assetManager.load(checkForHDPI(entry.getKey()), entry.getValue());
      }

      TextureLoader.TextureParameter parameter = new TextureLoader.TextureParameter();
      parameter.genMipMaps = true;
      parameter.minFilter = MipMapNearestNearest;
      parameter.magFilter = Linear;
      assetManager.load(checkForHDPI("elevator/shaft.png"), Texture.class, parameter);
      assetManager.load(checkForHDPI("elevator/empty.png"), Texture.class, parameter);

      assetManager.setErrorListener(new AssetErrorListener() {
        public void error(String fileName, Class type, Throwable throwable) {
          throw new RuntimeException("Error loading: " + fileName, throwable);
        }
      });
    }

    return assetManager;
  }

  private static String checkForHDPI(String fileName) {
    if (Display.isHDPIMode()) {
      FileHandle file = Gdx.files.internal(fileName);
      String hdpiFileName = file.parent() + "/" + file.nameWithoutExtension() + "-hd." + file.extension();
      Gdx.app.error(TAG, "Looking for: " + hdpiFileName + ", found: " + Gdx.files.internal(hdpiFileName).exists());
      if (Gdx.files.internal(hdpiFileName).exists()) {
        return hdpiFileName;
      }
    }

    return fileName;
  }

  public static Skin skin(String s) {
    return assetManager().get(s, Skin.class);
  }

  public static BitmapFont bitmapFont(String s) {
    return assetManager().get(s, BitmapFont.class);
  }

  public static Sound sound(String s) {
    return assetManager().get(s, Sound.class);
  }

  public static TextureAtlas textureAtlas(String s) {
    return assetManager().get(checkForHDPI(s), TextureAtlas.class);
  }

  public static Texture texture(String s) {
    return assetManager().get(checkForHDPI(s), Texture.class);
  }

  public static void dispose() {
    assetManager.dispose();
  }

  public static TextureAtlas.AtlasRegion textureFromAtlas(String textureName, String atlasName) {
    return assetManager().get(checkForHDPI(atlasName), TextureAtlas.class).findRegion(textureName);
  }

  public static NinePatch ninePatch(String fileName) {
    return ninePatch(fileName, Color.WHITE);
  }

  public static NinePatch ninePatch(String fileName, Color color) {
    return new NinePatch(texture(fileName), color);
  }

  public static Sprite sprite(String fileName) {
    return new Sprite(texture(fileName));
  }

  public static void setGuiSkin(Skin guiSkin) {
    skin = guiSkin;
  }

  public static Skin getGuiSkin() {
    return skin;
  }
}
