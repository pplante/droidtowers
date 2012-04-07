/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.Rain;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.events.WeatherState;
import com.happydroids.droidtowers.events.WeatherStateChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;

public class RainLayer extends GameLayer {
  private final GameGrid gameGrid;
  private final WeatherService weatherService;

  public RainLayer(GameGrid gameGrid, WeatherService weatherService) {
    this.gameGrid = gameGrid;
    this.weatherService = weatherService;
    gameGrid.events().register(this);
    weatherService.events().register(this);
  }

  private void updateRain() {
    removeAllChildren();

    if (weatherService.currentState() == WeatherState.RAINING) {
      addChild(new Rain(gameGrid));
      addChild(new Rain(gameGrid));
    }
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    updateRain();
  }

  @Subscribe
  public void WeatherService_onWeatherChange(WeatherStateChangeEvent event) {
    updateRain();
  }
}
