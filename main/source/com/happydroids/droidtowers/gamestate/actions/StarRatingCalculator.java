/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.StatLog;

public class StarRatingCalculator extends GameGridAction {

  private final StatLog roomDesirability;
  private Achievement dubai7StarWonder;
  private float maxStars;

  public StarRatingCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);

    roomDesirability = new StatLog();
    dubai7StarWonder = AchievementEngine.instance().findById("dubai-7-star-wonder");
    maxStars = 5f;
  }

  @Override
  public void run() {
    roomDesirability.reset(gameGrid.getObjects().size);
    Array<GridObject> objects = gameGrid.getObjects();
    for (int i = 0, objectsSize = objects.size; i < objectsSize; i++) {
      GridObject gridObject = objects.get(i);
      roomDesirability.record(gridObject.getDesirability());
    }

    Player player = Player.instance();
    float roomDesirabilityAverage;
    float populationFilled = 0;
    float jobsFilled = 0;
    float incomeRatio = 0;
    roomDesirabilityAverage = roomDesirability.getAverage();
    if (player.getMaxPopulation() > 0) {
      populationFilled = player.getPopulationResidency() / (float) player.getMaxPopulation();
    }

    if (player.getJobsMax() > 0) {
      jobsFilled = player.getJobsFilled() / (float) player.getJobsMax();
    }

    if (player.getCurrentIncome() > 0) {
      incomeRatio = (player.getCurrentIncome() - player.getCurrentExpenses()) / (float) (player.getCurrentIncome() + player.getCurrentExpenses());
    }

    player.setBudgetRating(incomeRatio);
    player.setEmploymentRating(jobsFilled);
    player.setPopulationRating(populationFilled);
    player.setDesirabilityRating(roomDesirabilityAverage);

    float compositeRating = (roomDesirabilityAverage * 0.33f) + (((populationFilled + jobsFilled) * 0.5f) * 0.33f) + (incomeRatio * 0.33f);

    if (dubai7StarWonder.isCompleted()) {
      maxStars = 7f;
    }

    float starRating = MathUtils.clamp(compositeRating * maxStars, 0f, maxStars);
    player.setStarRating(starRating);
  }

  public float calculateJobsRating() {
    return 0f;
  }
}
