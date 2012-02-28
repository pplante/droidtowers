package com.unhappyrobot;

import com.google.common.eventbus.EventBus;

public class WeatherService {
  private static EventBus eventBus = new EventBus(WeatherService.class.getSimpleName());

  public WeatherService() {

  }

  public static EventBus events() {
    return eventBus;
  }
}
