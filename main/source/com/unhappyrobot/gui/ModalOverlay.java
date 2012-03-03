package com.unhappyrobot.gui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.unhappyrobot.tween.TweenSystem;

public class ModalOverlay extends WidgetGroup {
  public static final float TARGET_OPACITY = 0.5f;
  private HeadsUpDisplay headsUpDisplay;
  private Image background;

  private static ModalOverlay instance;

  public static void initialize(HeadsUpDisplay headsUpDisplay) {
    if (instance == null) {
      instance = new ModalOverlay(headsUpDisplay);
    }
  }

  public static ModalOverlay instance() {
    if (instance == null) {
      throw new RuntimeException("Must call initialize first!");
    }

    return instance;
  }

  private ModalOverlay(HeadsUpDisplay headsUpDisplay) {
    super();

    this.headsUpDisplay = headsUpDisplay;

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

  public void show() {
    headsUpDisplay.addActor(this);
    background.width = Gdx.graphics.getWidth();
    background.height = Gdx.graphics.getHeight();

    Timeline.createSequence()
            .push(Tween.set(background, WidgetAccessor.OPACITY).target(0f))
            .push(Tween.to(background, WidgetAccessor.OPACITY, 200).target(TARGET_OPACITY))
            .start(TweenSystem.getTweenManager());
  }

  public void hide() {
    Timeline.createSequence()
            .push(Tween.set(background, WidgetAccessor.OPACITY).target(TARGET_OPACITY))
            .push(Tween.to(background, WidgetAccessor.OPACITY, 200).target(0f))
            .addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                markToRemove(true);
              }
            })
            .start(TweenSystem.getTweenManager());
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
