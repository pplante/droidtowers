/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.pipeline;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.Set;

public class GenerateAssetManagerFileList {
  private static FileHandle assetsDir = new FileHandle("assets/");
  private static Set<String> managedFiles = Sets.newHashSet();

  public static void main(String[] args) {
    FileHandle template = new FileHandle("assets-raw/templates/TowerAssetManagerFilesList-template.java");

    addDirectoryToAssetManager("backgrounds/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("backgrounds/", ".png", Texture.class);
    addDirectoryToAssetManager("characters/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("fonts/", ".fnt", BitmapFont.class);
    addDirectoryToAssetManager("hud/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("rooms/", ".txt", TextureAtlas.class);
    addDirectoryToAssetManager("sound/effects/", ".wav", Sound.class);

    addFileEntry(assetsDir.child("default-skin.ui"), Skin.class);
    addFileEntry(assetsDir.child("transport.txt"), TextureAtlas.class);
    addFileEntry(assetsDir.child("rain-drop.png"), Texture.class);
    addFileEntry(assetsDir.child("decals.png"), Texture.class);

    String javaFileContent = template.readString();
    javaFileContent = javaFileContent.replace("// REPLACEME", Joiner.on("\n").join(managedFiles));

    FileHandle outputFile = new FileHandle("../main/source/com/unhappyrobot/TowerAssetManagerFilesList.java");
    outputFile.writeString(javaFileContent, false);

    System.out.println("Generated: " + outputFile.path());
  }

  private static void addDirectoryToAssetManager(String folder, String suffix, Class clazz) {
    for (FileHandle child : assetsDir.child(folder).list(suffix)) {
      addFileEntry(child, clazz);
    }
  }

  private static void addFileEntry(FileHandle child, Class clazz) {
    if (!child.exists()) {
      throw new RuntimeException("File not found: " + child.path());
    }
    managedFiles.add(String.format("files.put(\"%s\", %s.class);", child.path().replace("assets/", ""), clazz.getSimpleName()));
  }

}
