/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.disk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class FileResolverMultiplexer implements FileHandleResolver {
  @Override public FileHandle resolve(String fileName) {
    FileHandle fileHandle = Gdx.files.internal(fileName);
    if (fileHandle != null && fileHandle.exists()) {
      return fileHandle;
    }

    return Gdx.files.absolute(fileName);
  }
}
