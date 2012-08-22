/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.badlogic.gdx.graphics.Color;
import com.happydroids.droidtowers.utils.Random;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public enum WeatherState {
  SUNNY(rgba("#7ab7dd"), Color.WHITE),
  RAINING(rgba("#374153"), rgba("#111111")),
  SNOWING(rgba("#8b99b2"), rgba("#b8b8b8"));

  public final Color skyColor;
  public final Color cloudColor;

  WeatherState(Color skyColor, Color cloudColor) {
    this.skyColor = skyColor;
    this.cloudColor = cloudColor;
  }

  public static WeatherState random() {
    WeatherState[] states = WeatherState.values();

    return states[Random.randomInt(states.length)];
  }
}

