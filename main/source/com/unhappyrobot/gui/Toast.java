package com.unhappyrobot.gui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.TowerGame;

public class Toast extends Table implements Tweenable {
  private static Pixmap pixmap;
  private static NinePatch background;
  private final Label label;
  private float alpha;

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

    Tween.to(this, ALPHA, 500, Linear.INOUT).target(1.0f).addToManager(TowerGame.getTweenManager()).addCompleteCallback(new TweenCallback() {
      public void tweenEventOccured(Types eventType, Tween tween) {
        fadeOut();
      }
    });
  }

  private void fadeOut() {
    Tween.to(this, ALPHA, 250, Linear.INOUT).target(0f).delay(2000).addToManager(TowerGame.getTweenManager());
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, alpha);
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
