/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public abstract class PixmapGenerator {
  private Texture texture;
  private NinePatch ninePatch;
  private Pixmap pixmap;
  private boolean loaded;

  protected PixmapGenerator() {
    RuntimePixmapManager.manage(this);
  }

  private void reloadIfNeeded() {
    if (!loaded) {
      reload();
    }
  }

  public void reload() {
    release();

    pixmap = generate();
    texture = new Texture(pixmap, false);
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    ninePatch = new NinePatch(texture);
    loaded = true;
  }

  private void release() {
    loaded = false;
    ninePatch = null;
    if (texture != null) {
      texture.dispose();
    }
  }

  public Texture getTexture() {
    reloadIfNeeded();

    return texture;
  }

  public NinePatch getNinePatch() {
    reloadIfNeeded();

    return ninePatch;
  }

  protected abstract Pixmap generate();

  public void dispose() {
    RuntimePixmapManager.remove(this);

    release();
  }
}
