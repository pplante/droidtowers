/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.designer.input.CanvasTouchListener;

public class Canvas extends WidgetGroup {
  private final LayeredDrawable background;
  private final CanvasTouchListener touchListener;
  private final ShapeRenderer shapeRenderer;

  public Canvas() {
    LayeredDrawable layers = new LayeredDrawable();
    layers.add(new DropShadow());
    layers.add(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.WHITE));

    background = layers;

    shapeRenderer = new ShapeRenderer();

    setTouchable(Touchable.enabled);
    touchListener = new CanvasTouchListener(this);
    addListener(touchListener);
  }

  public void add(Actor actor) {
    addActor(actor);

    actor.setTouchable(Touchable.disabled);
  }

  @Override public float getPrefWidth() {
    return 256;
  }

  @Override public float getPrefHeight() {
    return 256;
  }

  @Override public void draw(SpriteBatch batch, float parentAlpha) {
    batch.setColor(getColor());
    float scale = getScaleX();
    background.draw(batch, getX(), getY(), getWidth() * scale, getHeight() * scale);
    super.draw(batch, parentAlpha);
  }

  public CanvasTouchListener getTouchListener() {
    return touchListener;
  }
}
