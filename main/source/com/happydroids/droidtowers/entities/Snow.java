/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

public class Snow extends GameObject {
  public static final int RAIN_TEXURE_SIZE = 128;

  public Snow(Vector2 worldSize) {
    super();

    Texture rainDropTexture = TowerAssetManager.texture("snow.png");
    rainDropTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    setTexture(rainDropTexture);

    float width = worldSize.x + (Display.getBiggestScreenDimension() * 4) + (RAIN_TEXURE_SIZE * 2);
    float height = worldSize.y + (Display.getBiggestScreenDimension() * 4) + (RAIN_TEXURE_SIZE * 2);
    setPosition(-Display.getBiggestScreenDimension() * 2, 0);
    setSize(width, height);
    setRegion(0, 0, width / RAIN_TEXURE_SIZE, height / RAIN_TEXURE_SIZE);

    Tween.to(this, GameObjectAccessor.TEXTURE_VV2, Random.randomInt(60000, 64000))
            .ease(Linear.INOUT)
            .target(-getV2(), 0f)
            .repeat(Tween.INFINITY, 0)
            .setCallback(new TweenCallback() {
              public void onEvent(int eventType, BaseTween source) {
                setPosition(Random.randomInt(-Display.getBiggestScreenDimension() * 2, -Display.getBiggestScreenDimension()), 0);
              }
            })
            .setCallbackTriggers(TweenCallback.BEGIN)
            .start(TweenSystem.manager());
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    float alpha = 1f - SceneManager.activeScene().getCamera().zoom / CameraController.ZOOM_MAX;
    if (alpha > 0.25f) {
      visible = true;
      setColor(1f, 1f, 1f, alpha);
    } else {
      visible = false;
    }
  }
}
