/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.ProviderType;

public class TestGridObjectType extends GridObjectType {
  public TestGridObjectType() {
    name = TestGridObjectType.class.getSimpleName();
    id = name;
    width = 1;
    height = 1;
  }

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new TestGridObject(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return false;
  }

  public void setAtlasFilename(String atlasFilename) {
    this.atlasFilename = atlasFilename;
  }

  public void setCanShareSpace(boolean canShareSpace) {
    this.canShareSpace = canShareSpace;
  }

  public void setCoins(int coins) {
    this.coins = coins;
  }

  public void setExperienceAward(int experienceAward) {
    this.experienceAward = experienceAward;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setImageFilename(String imageFilename) {
    this.imageFilename = imageFilename;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNoiseLevel(float noiseLevel) {
    this.noiseLevel = noiseLevel;
  }

  public void setProvides(ProviderType provides) {
    this.provides = provides;
  }

  public void setWidth(int width) {
    this.width = width;
  }
}
