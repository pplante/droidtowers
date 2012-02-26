package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.tween.GameObjectAccessor;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.Random;

public class Rain extends GameObject {
  public Rain(Vector2 worldSize) {
    super();

    Texture rainDropTexture = new Texture(Gdx.files.internal("rain-drop.png"));
    rainDropTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    setTexture(rainDropTexture);

    setSize(worldSize.x, worldSize.y);
    setRegion(0, 0, worldSize.x / 64, worldSize.y / 64);

    setV(0f);
    setV2(20.0f);

    Tween.to(this, GameObjectAccessor.TEXTURE_VV2, Random.randomInt(8000, 10000))
            .target(-20f, 0f)
            .repeat(Tween.INFINITY, 0)
            .addCallback(TweenCallback.EventType.BEGIN, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                System.out.println("source = " + source);
                setPosition(Random.randomInt(0, 32), 0);
                setOpacity(Random.randomFloat());
              }
            })
            .start(TweenSystem.getTweenManager());
  }
}
