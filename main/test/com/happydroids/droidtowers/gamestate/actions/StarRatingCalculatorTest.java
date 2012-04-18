/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.NonGLTestRunner;
import com.happydroids.droidtowers.achievements.TestGridObjectType;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.TestGameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;
import static com.happydroids.droidtowers.types.ProviderType.HOUSING;

@RunWith(NonGLTestRunner.class)
public class StarRatingCalculatorTest {
  @Before
  public void setUp() {
    Player.setInstance(new Player(1));
  }

  @Test
  public void calculateJobsRating_shouldReturnZero_whenNoCommercialObjectsExist() {
    GameGrid gameGrid = makeGameGridWith(HOUSING);
    Player.instance().setPopulationResidency(20);

    StarRatingCalculator starRatingCalculator = new StarRatingCalculator(gameGrid, 1f);
    expect(starRatingCalculator.calculateJobsRating()).toEqual(0f);
  }

  private GameGrid makeGameGridWith(ProviderType... types) {
    GameGrid grid = new TestGameGrid();

    for (ProviderType type : types) {
      TestGridObjectType objectType = new TestGridObjectType();
      objectType.setProvides(type);

      grid.addObject(objectType.makeGridObject(grid));
    }

    return grid;
  }
}
