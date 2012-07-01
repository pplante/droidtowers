/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.happydroids.droidtowers.platform.Display;

public class ResolutionIndependentAtlas extends TextureAtlas {
  public ResolutionIndependentAtlas(FileHandle packFile) {
    super(packFile);
  }

  @Override
  public AtlasRegion findRegion(String name) {
    if (Display.isXHDPIMode()) {
      AtlasRegion hdpiRegion = super.findRegion("hdpi/" + name);
      if (hdpiRegion != null) {
        return hdpiRegion;
      }
    }

    return super.findRegion(name);
  }
}
