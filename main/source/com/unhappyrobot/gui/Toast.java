package com.unhappyrobot.gui;

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
import com.unhappyrobot.tween.TweenSystem;

public class Toast extends Table {
  private static Pixmap pixmap;
  private static NinePatch background;
  private final Label label;
  private final HeadsUpDisplay headsUpDisplay;

  public Toast(HeadsUpDisplay headsUpDisplay) {
    this.headsUpDisplay = headsUpDisplay;
    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
      pixmap.setColor(new Color(0, 0, 0, 0.65f));
      pixmap.fill();

      background = new NinePatch(new Texture(pixmap));
    }

    visible = false;
    label = new Label(headsUpDisplay.getGuiSkin());

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

    x = (headsUpDisplay.getStage().width() - width) / 2;
    y = headsUpDisplay.getStage().height() - height - 10;

    color.a = 0f;
    visible = true;

    fadeIn();
  }

  protected void fadeIn() {
    Tween.to(this, WidgetAccessor.OPACITY, 500).target(1.0f).start(TweenSystem.getTweenManager()).addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        fadeOut();
      }
    });
  }

  protected void fadeOut() {
    Tween.to(this, WidgetAccessor.OPACITY, 250)
            .target(0f)
            .delay(3000)
            .addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                visible = false;
              }
            })
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
