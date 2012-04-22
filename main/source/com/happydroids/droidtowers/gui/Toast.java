/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.graphics.PixmapGenerator;
import com.happydroids.droidtowers.tween.TweenSystem;

import static com.happydroids.droidtowers.platform.Display.scale;

public class Toast extends Table {
  private final Label label;
  private final PixmapGenerator pixmapGenerator;

  public Toast() {
    pixmapGenerator = new PixmapGenerator() {
      @Override
      protected Pixmap generate() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(0, 0, 0, 0.65f));
        pixmap.fill();

        return pixmap;
      }
    };

    visible = false;
    label = FontManager.RobotoBold18.makeLabel("");

    defaults();
    setBackground(pixmapGenerator.getNinePatch());
    pad(scale(12));
    add(label);
    pack();
  }

  public void setMessage(String message) {
    label.setText(message);
  }


  public void show() {
    pack();

    x = (TowerGame.getActiveScene().getStage().width() - width) / 2;
    y = height + scale(10);

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

  @Override
  public void remove() {
    pixmapGenerator.dispose();

    super.remove();
  }
}
