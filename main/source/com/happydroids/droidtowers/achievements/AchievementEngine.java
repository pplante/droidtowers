/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
      FileHandle fileHandle = Gdx.files.internal("params/achievements.json");
      ObjectMapper mapper = TowerGameService.instance().getObjectMapper();
      achievements = mapper.readValue(fileHandle.reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Achievement.class));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }

  @Subscribe
  public void GameEvent_handleGridObjectEvent(GridObjectChangedEvent event) {
    if (!event.nameOfParamChanged.equals("placementState") || !event.gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
      return;
    }
    System.out.println("event = " + event);
    List<String> summary = Lists.newArrayList();

    Iterator<Achievement> achievementIterator = achievements.iterator();
    while (achievementIterator.hasNext()) {
      Achievement achievement = achievementIterator.next();
      System.out.println(achievement);
      if (achievement.isCompleted(gameGrid) && !completedAchievements.contains(achievement)) {
        String rewardSummary = achievement.giveReward();

        if (rewardSummary != null) {
          summary.add(rewardSummary);
        }

        AchievementNotification notification = new AchievementNotification(achievement);
        notification.show();

        completedAchievements.add(achievement);

        achievementIterator.remove();
      }
    }
  }

  public Set<Achievement> getCompletedAchievements() {
    return completedAchievements;
  }

  public void loadCompletedAchievements(List<String> achievementIds) {
    for (Achievement achievement : achievements) {
      achievement.setCompleted(false);

      for (AchievementReward reward : achievement.getRewards()) {
        if (reward.getType().equals(RewardType.UNLOCK) && reward.getThing().equals(AchievementThing.OBJECT_TYPE)) {
          System.out.println("Reset: " + reward.getRewardString());
          reward.getThingObjectType().setLocked(true);
        }
      }
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
}
