package com.unhappyrobot;

import com.google.common.eventbus.EventBus;
import com.unhappyrobot.events.WeatherState;
import com.unhappyrobot.events.WeatherStateChangeEvent;

public class WeatherService {
  private static WeatherService instance;
  private static EventBus eventBus = new EventBus(WeatherService.class.getSimpleName());
  private WeatherState currentWeatherState;
  private float timeSinceChange;

  public WeatherService() {
    currentWeatherState = WeatherState.SUNNY;
  }

  public static EventBus events() {
    return eventBus;
  }

  public static WeatherService instance() {
    if (instance == null) {
      instance = new WeatherService();
    }

    return instance;
  }

  public WeatherState currentState() {
    return currentWeatherState;
  }

  public void update(float deltaTime) {
    timeSinceChange += deltaTime;

    if (timeSinceChange > TowerConsts.WEATHER_SERVICE_STATE_CHANGE_FREQUENCY) {
      timeSinceChange = 0f;

      currentWeatherState = WeatherState.random();
      eventBus.post(new WeatherStateChangeEvent());
    }
  }
}
