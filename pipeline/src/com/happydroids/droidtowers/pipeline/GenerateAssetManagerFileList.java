/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pipeline;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.utils.PNG;

import java.io.IOException;

public class GenerateAssetManagerFileList {
  private static FileHandle assetsDir = new FileHandle("assets/");
  private static AssetList managedFiles = new AssetList();

  public static void main(String[] args) {
    GdxNativesLoader.load();

    FileHandle template = new FileHandle("assets-raw/templates/TowerAssetManagerFilesList-template.coffee");

    preloadEntry(assetsDir.child("happy-droid.png"), Texture.class);
    preloadEntry(assetsDir.child("default-skin.ui"), Skin.class);
    preloadEntry(assetsDir.child("backgrounds/clouds.txt"), TextureAtlas.class);
    preloadEntry(assetsDir.child("hud/menus.txt"), TextureAtlas.class);
    preloadEntry(assetsDir.child("hud/buttons.txt"), TextureAtlas.class);
    preloadEntry(assetsDir.child("hud/window-bg.png"), Texture.class);
    preloadEntry(assetsDir.child("hud/toast-bg.png"), Texture.class);
    addDirectoryToPreloader("swatches/", ".png", Texture.class);

    addDirectoryToAssetManager("backgrounds/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("movies/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("backgrounds/", ".png", Texture.class);
    addDirectoryToAssetManager("fonts/", ".fnt", BitmapFont.class);
    addDirectoryToAssetManager("hud/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("rooms/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("sound/effects/", ".wav", Sound.class);

    addFileEntry(assetsDir.child("characters.txt"), TextureAtlas.class);
    addFileEntry(assetsDir.child("transport.txt"), TextureAtlas.class);
    addFileEntry(assetsDir.child("rain-drop.png"), Texture.class);
    addFileEntry(assetsDir.child("hud/star.png"), Texture.class);
    addFileEntry(assetsDir.child("hud/star-white.png"), Texture.class);
    addFileEntry(assetsDir.child("hud/status-bar-bg.png"), Texture.class);
    addFileEntry(assetsDir.child("decals.png"), Texture.class);

    FileHandle swatchesDir = assetsDir.child("swatches");
    if (!swatchesDir.exists()) {
      swatchesDir.mkdirs();
    }

    makeSwatch(swatchesDir, "swatch-white.png", Color.WHITE);

    for (Overlays overlay : Overlays.values()) {
      makeSwatch(swatchesDir, overlay.getSwatchFilename(), overlay.getColor(1f));
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      new FileHandle("../android/assets/assets.json").writeString(mapper.writeValueAsString(managedFiles), false);
    } catch (IOException e) {
      e.printStackTrace();
    }

//    String javaFileContent = template.readString();
//    javaFileContent = javaFileContent.replace("// REPLACEME", Joiner.on("\n").join(managedFiles));
//    FileHandle outputFile = new FileHandle("../main/source/com/happydroids/droidtowers/TowerAssetManagerFilesList.java");
//    outputFile.writeString(javaFileContent, false);
//    System.out.println("Generated: " + outputFile.path());
  }

  private static void makeSwatch(FileHandle swatchesDir, String swatchFilename, Color color) {
    FileHandle swatchFile = swatchesDir.child(swatchFilename);
    if (swatchFile.exists()) return;

    try {
      Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGB888);
      pixmap.setColor(color);
      pixmap.fill();

      byte[] bytes = PNG.toPNG(pixmap);
      swatchFile.writeBytes(bytes, false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void addDirectoryToPreloader(String folder, String suffix, Class clazz) {
    for (FileHandle child : assetsDir.child(folder).list(suffix)) {
      preloadEntry(child, clazz);
    }
  }

  private static void preloadEntry(FileHandle child, Class clazz) {
    if (!child.exists()) {
      throw new RuntimeException("File not found: " + child.path());
    } else if (child.name().contains("-hd")) {
      System.out.println("Skipping HD asset: " + child.path());
      return;
    }


    managedFiles.preload(child.path().replace("assets/", ""), checkForHDVersion(child), clazz);
  }

  private static void addDirectoryToAssetManager(String folder, String suffix, Class clazz) {
    for (FileHandle child : assetsDir.child(folder).list(suffix)) {
      addFileEntry(child, clazz);
    }
  }

  private static void addFileEntry(FileHandle child, Class clazz) {
    if (!child.exists()) {
      throw new RuntimeException("File not found: " + child.path());
    } else if (child.name().contains("-hd")) {
      System.out.println("Skipping HD asset: " + child.path());
      return;
    }

    managedFiles.normal(child.path().replace("assets/", ""), checkForHDVersion(child), clazz);
  }

  public static String checkForHDVersion(FileHandle fileHandle) {
    String hdpiFileName = fileHandle.parent() + "/" + fileHandle.nameWithoutExtension() + "-hd." + fileHandle.extension();
    if (hdpiFileName.startsWith("/")) {
      hdpiFileName = hdpiFileName.substring(1);
    }

    if (new FileHandle(hdpiFileName).exists()) {
      return hdpiFileName.replace("assets/", "");
    }

    return null;
  }
}
