package com.unhappyrobot.graphics;

import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.WeatherService;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.Rain;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.WeatherState;
import com.unhappyrobot.events.WeatherStateChangeEvent;
import com.unhappyrobot.grid.GameGrid;

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
