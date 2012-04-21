/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.GridObjectPlacementState;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.events.GridObjectChangedEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.AchievementNotification;
import com.happydroids.droidtowers.gui.TutorialStepNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AchievementEngine {
  private static AchievementEngine instance;
  private List<Achievement> achievements;
  private Set<Achievement> completedAchievements;
  private GameGrid gameGrid;

  public static AchievementEngine instance() {
    if (instance == null) {
      instance = new AchievementEngine();
    }

    return instance;
  }

  protected AchievementEngine() {
    completedAchievements = Sets.newHashSet();

    try {
      ObjectMapper mapper = TowerGameService.instance().getObjectMapper();
      achievements = mapper.readValue(Gdx.files.internal("params/achievements.json").reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Achievement.class));

      List<Achievement> tutorialSteps = mapper.readValue(Gdx.files.internal("params/tutorial-steps.json").reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, TutorialStep.class));
      achievements.addAll(tutorialSteps);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }

  public void checkAchievements() {
    Iterator<Achievement> achievementIterator = achievements.iterator();
    while (achievementIterator.hasNext()) {
      Achievement achievement = achievementIterator.next();
      if (achievement.isCompleted(gameGrid) && !completedAchievements.contains(achievement)) {
        completeAchievement(achievement);

        achievementIterator.remove();
      }
    }
  }

  public void completeAchievement(Achievement achievement) {
    if (completedAchievements.contains(achievement)) {
      return;
    }

    achievement.setCompleted(true);
    achievement.giveReward();

    if (achievement instanceof TutorialStep) {
      new TutorialStepNotification((TutorialStep) achievement).show();
    } else {
      new AchievementNotification(achievement).show();
    }

    completedAchievements.add(achievement);
  }

  public void completeAchievement(String achievementId) {
    for (Achievement achievement : achievements) {
      if (achievementId.equalsIgnoreCase(achievement.getId())) {
        completeAchievement(achievement);
        return;
      }
    }

    throw new RuntimeException("Could not find achievement called: " + achievementId);
  }

  public Set<Achievement> getCompletedAchievements() {
    return completedAchievements;
  }

  public void loadCompletedAchievements(List<String> achievementIds) {
    for (Achievement achievement : achievements) {
      achievement.resetState();
    }

    if (achievementIds == null) {
      return;
    }

    for (Achievement achievement : achievements) {
      if (achievementIds.contains(achievement.getId())) {
        achievement.setCompleted(true);
        achievement.giveReward();
        completedAchievements.add(achievement);
      }
    }
  }

  public void add(Achievement achievement) {
    achievements.add(achievement);
  }

  public boolean hasGameGrid() {
    return gameGrid != null;
  }

  public void unregisterGameGrid() {
    if (gameGrid != null) {
      gameGrid.events().unregister(AchievementEngine.instance());
    }

    gameGrid = null;
  }

  public void registerGameGrid(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    if (gameGrid != null) {
      gameGrid.events().register(AchievementEngine.instance());
    }
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Subscribe
  public void GameEvent_handleGridObjectEvent(GridObjectChangedEvent event) {
    if (event.nameOfParamChanged.equals("placementState") && event.gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
      return;
    }

    checkAchievements();
  }

  @Subscribe
  public void Elevator_onHeightChange(ElevatorHeightChangeEvent event) {
    completeAchievement("tutorial-finished");
  }
}
