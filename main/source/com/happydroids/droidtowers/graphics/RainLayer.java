/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.Rain;
import com.happydroids.droidtowers.entities.Snow;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.events.WeatherState;
import com.happydroids.droidtowers.events.WeatherStateChangeEvent;

public class RainLayer extends GameLayer implements RespondsToWorldSizeChange {
  private final WeatherService weatherService;
  private Vector2 worldSize;

  public RainLayer(WeatherService weatherService) {
    this.weatherService = weatherService;
    weatherService.events().register(this);
  }

  private void updateRain() {
    removeAllChildren();

    if (weatherService.currentState() == WeatherState.RAINING) {
      addChild(new Rain(worldSize));
    } else if (weatherService.currentState() == WeatherState.SNOWING) {
      addChild(new Snow(worldSize));
    }
  }

  @Subscribe
  public void WeatherService_onWeatherChange(WeatherStateChangeEvent event) {
    updateRain();
  }

  @Override
  public void updateWorldSize(Vector2 worldSize) {
    this.worldSize = worldSize;
    updateRain();
  }
}
