/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

public abstract class DroidSplashScene extends Scene {
  private GameObject droid;
  private Label statusLabel;

  @Override
  public void create(Object... args) {
    getCamera().position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    getCamera().zoom = CameraController.ZOOM_MIN;

    InputSystem.instance().bind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);

    droid = new GameObject(new Texture("happy-droid.png"));
    droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
    Tween.to(droid, GameObjectAccessor.OPACITY, 1000)
            .target(0f)
            .setCallback(new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .repeat(Tween.INFINITY, 100)
            .start(TweenSystem.getTweenManager());

    statusLabel = FontManager.Roboto64.makeLabel("working...");
    statusLabel.x = 25;
    statusLabel.y = 25;
  }

  protected void setStatusText(String statusText) {
    statusLabel.setText(statusText);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    getSpriteBatch().begin();
    droid.draw(getSpriteBatch());
    statusLabel.draw(getSpriteBatch(), 1f);
    getSpriteBatch().end();
  }

  @Override
  public void dispose() {
    InputSystem.instance().unbind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);
  }

  private InputCallback goBackHomeCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      handleBackButton();
      SceneManager.popScene();
      return true;
    }
  };

  abstract protected void handleBackButton();
}
