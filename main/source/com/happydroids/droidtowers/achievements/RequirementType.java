/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

public enum RequirementType {
  POPULATION("Population"),
  BUILD("Build");

  private final String label;

  RequirementType(String label) {
    this.label = label;
  }
}
