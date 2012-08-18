/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.designer.input.CanvasTouchListener;

public class Canvas extends WidgetGroup {
  private final NinePatch background;
  private final CanvasTouchListener touchListener;

  public Canvas() {
    background = TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH);

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
    background.draw(batch, getX() * scale, getY() * scale, getWidth() * scale, getHeight() * scale);

    super.draw(batch, parentAlpha);
  }

  public CanvasTouchListener getTouchListener() {
    return touchListener;
  }
}
