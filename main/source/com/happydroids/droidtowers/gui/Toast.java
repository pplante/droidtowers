/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tween.TweenSystem;

public class Toast extends Table {
  private final Label label;

  public Toast() {
    setVisible(false);
    label = FontManager.RobotoBold18.makeLabel("");

    defaults();
    setBackground(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Colors.DARKER_GRAY));
    pad(Display.devicePixel(12));
    add(label);
    pack();
    setTouchable(Touchable.enabled);
    addListener(new ClickListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        removeListener(this);
        fadeOut();
        return true;
      }
    });
  }

  public void setMessage(String message) {
    label.setText(message);
  }


  public void show() {
    pack();

    setX((SceneManager.activeScene().getStage().getWidth() - getWidth()) / 2);
    setY(getHeight() + Display.devicePixel(10));

    getColor().a = 0f;
    setVisible(true);

    fadeIn();
  }

  protected void fadeIn() {
    Tween.to(this, WidgetAccessor.OPACITY, 500)
            .target(1.0f)
            .start(TweenSystem.manager())
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
                remove();
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.manager());
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    if (getColor().a > 0.01f) {
      super.draw(batch, getColor().a);
    }
  }
}
