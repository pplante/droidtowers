/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.grid.GameGrid;

public class AchievementEngineCheck extends DesirabilityCalculator {
  public AchievementEngineCheck(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    AchievementEngine.instance().checkAchievements();
  }
}
