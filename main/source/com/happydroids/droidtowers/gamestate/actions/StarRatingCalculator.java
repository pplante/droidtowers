/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.grid.GameGrid;

public class StarRatingCalculator extends GameGridAction {
  public StarRatingCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
  }

  public float calculateJobsRating() {
    return 0f;
  }
}
