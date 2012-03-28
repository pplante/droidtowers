package com.unhappyrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TowerAssetManager {
  private static final String TAG = TowerAssetManager.class.getSimpleName();
  private static AssetManager assetManager;

  public static AssetManager assetManager() {
    if (assetManager == null) {
      assetManager = new AssetManager();
      Texture.setAssetManager(assetManager);

      assetManager.load("default-skin.ui", Skin.class);

      addDirectoryToAssetManager(assetManager, "backgrounds/", ".txt", TextureAtlas.class);
      addDirectoryToAssetManager(assetManager, "backgrounds/", ".png", Texture.class);
      addDirectoryToAssetManager(assetManager, "characters/", ".txt", TextureAtlas.class);
      addDirectoryToAssetManager(assetManager, "fonts/", ".fnt", BitmapFont.class);
      addDirectoryToAssetManager(assetManager, "hud/", ".txt", TextureAtlas.class);
      addDirectoryToAssetManager(assetManager, "rooms/", ".txt", TextureAtlas.class);
      addDirectoryToAssetManager(assetManager, "sound/effects/", ".wav", Sound.class);

      assetManager.load("transport.txt", TextureAtlas.class);
      assetManager.load("rain-drop.png", Texture.class);
      assetManager.load("decals.png", Texture.class);

      assetManager.setErrorListener(new AssetErrorListener() {
        public void error(String fileName, Class type, Throwable throwable) {
          Gdx.app.error(TAG, "Error loading file: " + fileName);
        }
      });

      do {
        assetManager.update();
      } while (!assetManager.isLoaded("default-skin.ui", Skin.class));
    }

    return assetManager;
  }

  private static void addDirectoryToAssetManager(AssetManager assetManager, String path, String pathSuffix, Class<?> clazz) {
    for (FileHandle fileHandle : Gdx.files.internal(path).list(pathSuffix)) {
      assetManager.load(fileHandle.path(), clazz);
    }
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
    return assetManager().get(s, TextureAtlas.class);
  }

  public static Texture texture(String s) {
    return assetManager().get(s, Texture.class);
  }

  public static void reset() {
    assetManager.dispose();
    assetManager = null;
  }
}
