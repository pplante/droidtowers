/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.badlogic.gdx.utils.Pool;

public class AssetLoadEvent implements Pool.Poolable {
  private String fileName;
  private Class type;

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setType(Class type) {
    this.type = type;
  }

  public Class getType() {
    return type;
  }

  @Override
  public void reset() {
    fileName = null;
    type = null;
  }
}
