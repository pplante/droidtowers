/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.GridObjectPlacementState;
import com.happydroids.droidtowers.events.GridObjectChangedEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.AchievementNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AchievementEngine {
  protected static AchievementEngine instance;
  protected List<Achievement> achievements;
  protected Set<Achievement> completedAchievements;
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
        complete(achievement);

        achievementIterator.remove();
      }
    }
  }

  public void complete(Achievement achievement) {
    if (completedAchievements.contains(achievement)) {
      return;
    }

    achievement.setCompleted(true);
    achievement.giveReward();

    displayNotification(achievement);

    completedAchievements.add(achievement);
  }

  protected void displayNotification(Achievement achievement) {
    new AchievementNotification(achievement).show();
  }

  public void complete(String achievementId) {
    for (Achievement achievement : achievements) {
      if (achievementId.equalsIgnoreCase(achievement.getId())) {
        complete(achievement);
        return;
      }
    }

    for (Achievement achievement : completedAchievements) {
      if (achievementId.equalsIgnoreCase(achievement.getId())) {
        return;
      }
    }

    throw new RuntimeException("Could not find achievement called: " + achievementId);
  }

  public Set<Achievement> getCompletedAchievements() {
    return completedAchievements;
  }

  public void loadCompletedAchievements(List<String> achievementIds) {
    resetState();

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
      gameGrid.events().unregister(this);
    }

    gameGrid = null;
  }

  public void registerGameGrid(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    if (gameGrid != null) {
      gameGrid.events().register(this);
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

  public void resetState() {
    if (!completedAchievements.isEmpty()) {
      for (Achievement completedAchievement : completedAchievements) {
        completedAchievement.setCompleted(false);
      }

      achievements.addAll(completedAchievements);
      completedAchievements.clear();
    }

    for (Achievement achievement : achievements) {
      achievement.resetState();
    }
  }
}
