/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.events.WeatherState;
import com.happydroids.droidtowers.events.WeatherStateChangeEvent;

public class WeatherService {
  private EventBus eventBus = new SafeEventBus(WeatherService.class.getSimpleName());
  private WeatherState currentWeatherState;
  private float timeSinceChange;

  public WeatherService() {
    currentWeatherState = WeatherState.SUNNY;
  }

  public EventBus events() {
    return eventBus;
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
