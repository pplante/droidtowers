/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.designer.input.CanvasTouchListener;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public class Canvas extends WidgetGroup {
  private final LayeredDrawable background;
  private final CanvasTouchListener touchListener;
  private final TiledDrawable floor;
  private final Texture ceiling;
  private final TiledDrawable walls;
  private Color wallsColor;
  private Color floorColor;
  private Color ceilingColor;

  public Canvas() {
    LayeredDrawable layers = new LayeredDrawable();
    layers.add(new DropShadow());
    layers.add(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.WHITE));

    background = layers;

    TextureAtlas textureAtlas = TowerAssetManager.textureAtlas("designer/floors.txt");
    walls = new TiledDrawable(textureAtlas.getRegions().random());
    floor = new TiledDrawable(textureAtlas.getRegions().random());
    ceiling = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);

    wallsColor = rgba("#b8d4ce");
    floorColor = rgba("#e0b048");
    ceilingColor = rgba("#d6d0bc");

    setTouchable(Touchable.enabled);
    touchListener = new CanvasTouchListener(this);
    addListener(touchListener);
  }

  public void add(Actor actor) {
    addActor(actor);

    actor.setTouchable(Touchable.disabled);
  }

  @Override public float getPrefWidth() {
    return 512;
  }

  @Override public float getPrefHeight() {
    return 128;
  }

  @Override public void draw(SpriteBatch batch, float parentAlpha) {
    batch.setColor(getColor());
    float scale = getScaleX();
    background.draw(batch, getX(), getY(), getWidth() * scale, getHeight() * scale);

    batch.setColor(wallsColor);
    walls.draw(batch, getX(), getY(), getWidth() * scale, getHeight() * scale);

    batch.setColor(floorColor);
    floor.draw(batch, getX(), getY(), getWidth() * scale, 10 * scale);

    batch.setColor(ceilingColor);
    batch.draw(ceiling, getX(), getY() + (getHeight() - 7) * scale, getWidth() * scale, 7 * scale);

    batch.setColor(Color.WHITE);
    super.draw(batch, parentAlpha);
  }

  public CanvasTouchListener getTouchListener() {
    return touchListener;
  }
}
