/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.math.MathUtils;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.StatLog;

public class StarRatingCalculator extends GameGridAction {

  private final StatLog roomDesirability;

  public StarRatingCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);

    roomDesirability = new StatLog();
  }

  @Override
  public void run() {
    roomDesirability.reset(gameGrid.getObjects().size());
    for (GridObject gridObject : gameGrid.getObjects()) {
      roomDesirability.record(gridObject.getDesirability());
    }

    Player player = Player.instance();
    float roomDesirabilityAverage;
    float populationFilled = 0;
    float jobsFilled = 0;
    float incomeRatio = 0;
    roomDesirabilityAverage = roomDesirability.getAverage();
    if (player.getMaxPopulation() > 0) {
      populationFilled = player.getPopulationResidency() / player.getMaxPopulation();
    }

    if (player.getJobsMax() > 0) {
      jobsFilled = player.getJobsFilled() / player.getJobsMax();
    }

    if (player.getCurrentIncome() > 0) {
      incomeRatio = (float) player.getCurrentExpenses() / player.getCurrentIncome();
    }

    float compositeRating = (roomDesirabilityAverage * 0.33f) + (((populationFilled + jobsFilled) * 0.5f) * 0.33f) + (incomeRatio * 0.33f);

    float starRating = MathUtils.clamp(compositeRating * 5f, 0f, 5f);
    player.setStarRating(starRating);
  }

  public float calculateJobsRating() {
    return 0f;
  }
}
