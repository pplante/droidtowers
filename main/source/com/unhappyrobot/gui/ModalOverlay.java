/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.unhappyrobot.TowerGame;

public class ModalOverlay extends WidgetGroup {
  public static final float TARGET_OPACITY = 0.5f;
  private HeadsUpDisplay headsUpDisplay;
  private Image background;

  private static ModalOverlay instance;

  public static ModalOverlay instance() {
    if (instance == null) {
      instance = new ModalOverlay();
    }

    return instance;
  }

  private ModalOverlay() {
    super();

    final Texture texture = new Texture(Gdx.files.internal("hud/modal-noise.png"));
    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    background = new TiledImage(texture);
    addActor(background);
  }

  public float getPrefWidth() {
    return Gdx.graphics.getWidth();
  }

  public float getPrefHeight() {
    return Gdx.graphics.getHeight();
  }

  public void show(Stage stage) {
    if (stage == null) {
      stage = TowerGame.getActiveScene().getStage();
    }

    stage.addActor(this);
    background.width = Gdx.graphics.getWidth();
    background.height = Gdx.graphics.getHeight();

    background.color.a = 0f;
    background.clearActions();
    background.action(FadeTo.$(TARGET_OPACITY, 0.25f));
  }

  public void hide() {
    background.clearActions();
    background.action(FadeOut.$(0.25f).setCompletionListener(new OnActionCompleted() {
      public void completed(Action action) {
        markToRemove(true);
      }
    }));
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    return true;
  }

  @Override
  public boolean touchMoved(float x, float y) {
    return true;
  }

  @Override
  public void touchDragged(float x, float y, int pointer) {

  }
}
