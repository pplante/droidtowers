/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.events;

import com.badlogic.gdx.graphics.Color;
import com.unhappyrobot.utils.Random;

public enum WeatherState {
  SUNNY(0.48f, 0.72f, 0.87f),
  RAINING(0.31f, 0.43f, 0.49f);

  public final Color color;

  WeatherState(float r, float g, float b) {
    color = new Color(r, g, b, 1f);
  }

  public static WeatherState random() {
    WeatherState[] states = WeatherState.values();

    return states[Random.randomInt(states.length)];
  }
}

