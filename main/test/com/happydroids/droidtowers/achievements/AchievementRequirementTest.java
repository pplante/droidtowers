/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.happydroids.droidtowers.NonGLTestRunner;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.TestGameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(NonGLTestRunner.class)
public class AchievementRequirementTest {

  private AchievementRequirement requirement;

  @Before
  public void setup() {
    requirement = new AchievementRequirement();
    requirement.setAmount(3);
    requirement.setType(RequirementType.BUILD);
    requirement.setThing(AchievementThing.PROVIDER_TYPE);
    requirement.setThingProviderTypes(ProviderType.HOTEL_ROOMS);
  }

  @Test
  public void isCompleted_shouldReturnFalse_whenProviderTypeRequirementIsNotMet() {
    expect(requirement.isCompleted(new TestGameGrid())).toBeFalse();
  }

  @Test
  public void isCompleted_shouldReturnTrue_whenProviderTypeRequirementIsMet() {
    TestGridObjectType testType = new TestGridObjectType();
    testType.setProvides(ProviderType.HOTEL_ROOMS);

    GameGrid gameGrid = new TestGameGrid();
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    expect(requirement.isCompleted(gameGrid)).toBeTrue();
  }

  @Test
  public void isCompleted_shouldReturnTrue_whenProviderTypeRequirementIsMetBySuperType() {
    TestGridObjectType testType = new TestGridObjectType();
    testType.setProvides(ProviderType.COMMERCIAL);

    GameGrid gameGrid = new TestGameGrid();
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    gameGrid.addObject(testType.makeGridObject(gameGrid));
    expect(requirement.isCompleted(gameGrid)).toBeTrue();
  }

}
