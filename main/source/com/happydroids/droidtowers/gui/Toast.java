/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.scenes.Scene;
import com.happydroids.droidtowers.tween.TweenSystem;

public class Toast extends Table {
  private static Pixmap pixmap;
  private static NinePatch background;
  private final Label label;

  public Toast() {
    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
      pixmap.setColor(new Color(0, 0, 0, 0.65f));
      pixmap.fill();

      background = new NinePatch(new Texture(pixmap));
    }

    visible = false;
    label = new Label(Scene.getGuiSkin());

    defaults();
    setBackground(background);
    pad(4);
    add(label);
    pack();
  }

  public void setMessage(String message) {
    label.setText(message);
  }


  public void show() {
    pack();

    x = (TowerGame.getActiveScene().getStage().width() - width) / 2;
    y = TowerGame.getActiveScene().getStage().height() - height - 10;

    color.a = 0f;
    visible = true;

    fadeIn();
  }

  protected void fadeIn() {
    Tween.to(this, WidgetAccessor.OPACITY, 500)
            .target(1.0f)
            .start(TweenSystem.getTweenManager())
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                fadeOut();
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE);
  }

  protected void fadeOut() {
    Tween.to(this, WidgetAccessor.OPACITY, 250)
            .target(0f)
            .delay(3000)
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                visible = false;
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    if (color.a > 0.01f) {
      super.draw(batch, color.a);
    }
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    visible = false;

    return true;
  }
}
