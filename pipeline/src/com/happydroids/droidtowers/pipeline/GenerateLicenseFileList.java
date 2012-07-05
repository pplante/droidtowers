/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pipeline;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

public class GenerateLicenseFileList {
  private static FileHandle assetsDir = new FileHandle("assets/");
  private static List<String> licenseFiles = Lists.newArrayList();

  public static void main(String[] args) {
    GdxNativesLoader.load();

    addDirectoryToAssetManager("licenses/", ".txt");

    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      new FileHandle("assets/licenses/index.json").writeString(mapper.writeValueAsString(licenseFiles), false);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void addDirectoryToAssetManager(String folder, String suffix) {
    for (FileHandle child : assetsDir.child(folder).list(suffix)) {
      System.out.println("Found license: " + child.path());
      licenseFiles.add(child.path().replace("assets/", ""));
    }
  }
}
