/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.events.AchievementCompletionEvent;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.AchievementNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.happydroids.droidtowers.achievements.RequirementType.ADD_NEIGHBOR;
import static com.happydroids.droidtowers.achievements.RequirementType.HAPPYDROIDS_CONNECT;

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
      eventBus = new SafeEventBus();
      ObjectMapper mapper = TowerGameService.instance().getObjectMapper();
      achievements = mapper.readValue(Gdx.files.internal("params/achievements.json").reader(), mapper.getTypeFactory()
                                                                                                       .constructCollectionType(ArrayList.class, Achievement.class));


      //noinspection PointlessBooleanExpression
      if (!TowerConsts.ENABLE_HAPPYDROIDS_CONNECT) {
        Iterator<Achievement> achievementIterator = achievements.iterator();
        while (achievementIterator.hasNext()) {
          Achievement achievement = achievementIterator.next();
          for (Requirement requirement : achievement.getRequirements()) {
            if (requirement.getType().equals(ADD_NEIGHBOR) || requirement.getType().equals(HAPPYDROIDS_CONNECT)) {
              achievementIterator.remove();
            }
          }
        }
      }

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
    for (int i = 0, achievementsSize = achievements.size(); i < achievementsSize; i++) {
      Achievement achievement = achievements.get(i);
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
    AchievementCompletionEvent event = Pools.obtain(AchievementCompletionEvent.class);
    event.setAchievement(achievement);
    eventBus.post(event);
    Pools.free(event);
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
    for (int i = 0, achievementsSize = achievements.size(); i < achievementsSize; i++) {
      Achievement achievement = achievements.get(i);
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
