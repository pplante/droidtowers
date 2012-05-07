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

    float roomDesirabilityAverage = roomDesirability.getAverage();
    Player player = Player.instance();
    float populationFilled = player.getPopulationResidency() / player.getMaxPopulation();
    float jobsFilled = player.getJobsFilled() / player.getJobsMax();
    float incomeRatio = (float) player.getCurrentExpenses() / player.getCurrentIncome();

    float compositeRating = (roomDesirabilityAverage * 0.33f) + (((populationFilled + jobsFilled) * 0.5f) * 0.33f) + (incomeRatio * 0.33f);

    float starRating = MathUtils.clamp(compositeRating * 5f, 0f, 5f);
    player.setStarRating(starRating);
  }

  public float calculateJobsRating() {
    return 0f;
  }
}
