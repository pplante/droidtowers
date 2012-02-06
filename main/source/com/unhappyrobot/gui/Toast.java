package com.unhappyrobot.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.TowerGame;

public class Toast extends Table {
  private static Pixmap pixmap;
  private static NinePatch background;
  private final Label label;
  float alpha;

  public Toast() {
    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
      pixmap.setColor(new Color(0, 0, 0, 0.65f));
      pixmap.fill();

      background = new NinePatch(new Texture(pixmap));
    }


    label = new Label(HeadsUpDisplay.getInstance().getGuiSkin());

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

    x = (Gdx.graphics.getWidth() - width) / 2;
    y = Gdx.graphics.getHeight() - height - 10;

    alpha = 0;

    fadeIn();
  }

  protected void fadeIn() {
    Tween.to(this, ToastAccessor.OPACITY, 500).target(1.0f).start(TowerGame.getTweenManager()).addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        fadeOut();
      }
    });
  }

  private void fadeOut() {
    Tween.to(this, ToastAccessor.OPACITY, 250).target(0f).delay(2000).start(TowerGame.getTweenManager());
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    if (alpha > 0.01f) {
      super.draw(batch, alpha);
    }
  }

  public int getTweenValues(int tweenType, float[] returnValues) {
    switch (tweenType) {
      case ALPHA:
        returnValues[0] = alpha;
        return 1;
    }

    return 0;
  }


  public void onTweenUpdated(int tweenType, float[] newValues) {
    switch (tweenType) {
      case ALPHA:
        alpha = newValues[0];
        break;
    }
  }

  private static final int ALPHA = 1;
}
