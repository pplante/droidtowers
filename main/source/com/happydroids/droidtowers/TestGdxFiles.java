/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

/**
 * @author mzechner
 * @author Nathan Sweet
 */
public class TestGdxFiles implements Files {
  static public final String externalPath = System.getProperty("user.home") + "/";

  public FileHandle getFileHandle(String fileName, FileType type) {
    return new TestGdxFileHandle(fileName, type);
  }

  public FileHandle classpath(String path) {
    return new TestGdxFileHandle(path, FileType.Classpath);
  }

  public FileHandle internal(String path) {
    if (!path.contains("android/assets")) {
      if (new File("../android/assets").exists()) {
        path = "../android/assets/" + path;
      } else if (new File(".android/assets").exists()) {
        path = "./android/assets/" + path;
      }
    }

    return new TestGdxFileHandle(path, FileType.Internal);
  }

  public FileHandle external(String path) {
    return new TestGdxFileHandle(path, FileType.External);
  }

  public FileHandle absolute(String path) {
    return new TestGdxFileHandle(path, FileType.Absolute);
  }

  public FileHandle local(String path) {
    return new TestGdxFileHandle(path, FileType.Local);
  }

  public String getExternalStoragePath() {
    return externalPath;
  }

  public boolean isExternalStorageAvailable() {
    return true;
  }

  public String getLocalStoragePath() {
    return "";
  }

  public boolean isLocalStorageAvailable() {
    return true;
  }
}
