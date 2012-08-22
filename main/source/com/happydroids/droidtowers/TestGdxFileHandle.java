/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

public final class TestGdxFileHandle extends FileHandle {
  public TestGdxFileHandle(String fileName, Files.FileType type) {
    super(fileName, type);
  }

  public TestGdxFileHandle(File file, Files.FileType type) {
    super(file, type);
  }

  public FileHandle child(String name) {
    if (file.getPath().length() == 0) {
      return new TestGdxFileHandle(new File(name), type);
    }
    return new TestGdxFileHandle(new File(file, name), type);
  }

  public FileHandle parent() {
    File parent = file.getParentFile();
    if (parent == null) {
      if (type == Files.FileType.Absolute) {
        parent = new File("/");
      } else {
        parent = new File("");
      }
    }
    return new TestGdxFileHandle(parent, type);
  }

  public File file() {
    if (type == Files.FileType.External) {
      return new File(TestGdxFiles.externalPath, file.getPath());
    }
    return file;
  }
}
