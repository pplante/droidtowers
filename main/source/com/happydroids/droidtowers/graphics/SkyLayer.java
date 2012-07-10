/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.events.WeatherStateChangeEvent;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;

public class SkyLayer extends GameLayer implements RespondsToWorldSizeChange {
  private final WeatherService weatherService;
  private final GameObject sky;

  public SkyLayer(WeatherService weatherService) {
    super();
    this.weatherService = weatherService;

    weatherService.events().register(this);

    Texture texture = TowerAssetManager.texture("backgrounds/sky-gradient.png");
    sky = new GameObject(texture);
    sky.setColor(weatherService.currentState().skyColor);

    addChild(sky);
  }

  public void updateWorldSize(Vector2 worldSize) {
    sky.setPosition(-Display.getBiggestScreenDimension(), TowerConsts.GROUND_HEIGHT);
    sky.setSize(worldSize.x + (Display.getBiggestScreenDimension() * 4), worldSize.y + Display.getBiggestScreenDimension());
    sky.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
  }

  @Subscribe
  public void WeatherService_onWeatherChange(WeatherStateChangeEvent event) {
    Color tweenColor = weatherService.currentState().skyColor;

    Tween.to(sky, GameObjectAccessor.COLOR, TowerConsts.WEATHER_SERVICE_STATE_CHANGE_DURATION)
            .target(tweenColor.r, tweenColor.g, tweenColor.b, tweenColor.a)
            .start(TweenSystem.manager());
  }
}
