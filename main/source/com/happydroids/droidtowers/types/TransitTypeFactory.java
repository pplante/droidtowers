/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.google.common.collect.Lists;

public class TransitTypeFactory extends GridObjectTypeFactory<TransitType> {
  private static TransitTypeFactory instance;

  private TransitTypeFactory() {
    super(TransitType.class);

    objectTypes = Lists.newArrayList();
    objectTypes.addAll(ElevatorTypeFactory.instance().all());
    objectTypes.addAll(StairTypeFactory.instance().all());
  }

  public static TransitTypeFactory instance() {
    if (instance == null) {
      instance = new TransitTypeFactory();
    }

    return instance;
  }
}
