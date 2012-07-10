/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.events.AchievementCompletionEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.AchievementNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AchievementEngine {
  private static final String TAG = AchievementEngine.class.getSimpleName();

  protected static AchievementEngine instance;
  protected List<Achievement> achievements;
  protected EventBus eventBus;
  protected Map<String, Achievement> achievementsById;


  public static AchievementEngine instance() {
    if (instance == null) {
      instance = new AchievementEngine();
    }

    return instance;
  }

  protected AchievementEngine() {
    try {
      eventBus = new EventBus();
      ObjectMapper mapper = TowerGameService.instance().getObjectMapper();
      achievements = mapper.readValue(Gdx.files.internal("params/achievements.json").reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Achievement.class));
      achievementsById = Maps.newHashMap();

      for (Achievement achievement : achievements) {
        achievementsById.put(achievement.getId(), achievement);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }

  public void checkAchievements(GameGrid gameGrid) {
    Gdx.app.debug(TAG, "Checking achievements...");
    for (Achievement achievement : achievements) {
      achievement.checkRequirements(gameGrid);

      if (achievement.isCompleted()) {
        complete(achievement);
      }
    }
  }

  protected void complete(Achievement achievement) {
    if (achievement.isLocked() || achievement.hasGivenReward()) {
      return;
    }

    achievement.setCompleted(true);
  }

  public void displayNotification(Achievement achievement) {
    new AchievementNotification(achievement).show();
    eventBus.post(new AchievementCompletionEvent(achievement));
  }

  public void complete(String achievementId) {
    Achievement achievement = findById(achievementId);
    if (achievement != null) {
      complete(achievement);
      return;
    }

    throw new RuntimeException("Could not find achievement called: " + achievementId);
  }

  public void loadCompletedAchievements(List<String> achievementIds, GameGrid gameGrid) {
    resetState();

    if (achievementIds == null) {
      return;
    }

    for (Achievement achievement : achievements) {
      achievement.checkRequirements(gameGrid);

      if (achievementIds.contains(achievement.getId())) {
        achievement.setCompleted(true);
        achievement.unlockReward();
      }
    }
  }

  public void add(Achievement achievement) {
    achievements.add(achievement);
  }

  public void resetState() {
    for (Achievement completedAchievement : achievements) {
      completedAchievement.setCompleted(false);
    }

    for (Achievement achievement : achievements) {
      achievement.resetState();
    }
  }

  public void completeAll() {
    for (Achievement achievement : achievements) {
      achievement.setCompleted(true);
      achievement.unlockReward();
    }
  }

  public Achievement findById(String achievementId) {
    return achievementsById.get(achievementId);
  }

  public boolean hasPendingAwards() {
    for (Achievement achievement : achievements) {
      if (achievement.isCompleted() && !achievement.hasGivenReward()) {
        return true;
      }
    }

    return false;
  }

  public EventBus eventBus() {
    return eventBus;
  }
}
