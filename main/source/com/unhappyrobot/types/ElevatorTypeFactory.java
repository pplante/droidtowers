/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;

public class ElevatorTypeFactory extends GridObjectTypeFactory<ElevatorType> {
  private static ElevatorTypeFactory instance;

  private ElevatorTypeFactory() {
    super(ElevatorType.class);

    parseTypesFile(Gdx.files.internal("params/elevators.json"));
  }

  public static ElevatorTypeFactory instance() {
    if (instance == null) {
      instance = new ElevatorTypeFactory();
    }

    return instance;
  }
}
