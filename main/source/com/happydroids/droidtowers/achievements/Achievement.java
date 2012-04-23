/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

import static com.badlogic.gdx.Application.ApplicationType.Android;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Achievement {
  private static final String TAG = Achievement.class.getSimpleName();

  private String id;
  protected String name;
  protected String description;
  protected String descriptionMobile;
  private List<AchievementRequirement> requirements;
  protected List<AchievementReward> rewards;
  private boolean completed;
  private AchievementReward lockedBy;

  public Achievement() {

  }

  public Achievement(String name) {
    this.name = name;
    rewards = Lists.newArrayList();
    requirements = Lists.newArrayList();
  }

  public boolean requirementsMet(GameGrid gameGrid) {
    if (isLocked()) {
      return false;
    }

    if (requirements != null) {
      for (AchievementRequirement requirement : requirements) {
        if (!requirement.isCompleted(gameGrid)) {
          return false;
        }
      }
      return true;
    }

    return false;
  }

  public String giveReward() {
    if (completed) {
      if (rewards != null) {
        for (AchievementReward reward : rewards) {
          reward.give();
        }
      }
    }

    return toRewardString();
  }

  public void resetState() {
    Gdx.app.debug(TAG, "Reset: " + id);
    completed = false;

    if (rewards != null) {
      for (AchievementReward reward : rewards) {
        reward.resetState();
      }
    } else {
      if (TowerConsts.DEBUG) System.out.println("Achievement has no rewards: " + getId());
    }
  }

  public String toRewardString() {
    List<String> summary = Lists.newArrayList(String.format("Complete: %s!", name));
    if (rewards != null) {
      for (AchievementReward reward : rewards) {
        summary.add(reward.getRewardString());
      }
    }

    return Joiner.on("\n").join(summary);
  }

  public String getId() {
    return id;
  }

  void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public void addReward(AchievementReward reward) {
    rewards.add(reward);
  }

  public String getName() {
    return name;
  }

  public List<AchievementReward> getRewards() {
    return rewards;
  }

  public String getDescription() {
    if (descriptionMobile != null && Gdx.app.getType().equals(Android)) {
      return descriptionMobile;
    }

    return description;
  }

  @Override
  public String toString() {
    return "Achievement{" +
                   "completed=" + completed +
                   ", id='" + id + '\'' +
                   ", name='" + name + '\'' +
                   ", requirements=" + requirements +
                   ", rewards=" + rewards +
                   '}';
  }

  public boolean isLocked() {
    return lockedBy != null;
  }

  public void addLock(AchievementReward reward) {
    if (!isLocked()) {
      lockedBy = reward;
      Gdx.app.debug(TAG, id + " locked by " + lockedBy);
    } else {
      Gdx.app.debug(TAG, id + " is already locked by " + lockedBy);
    }
  }

  public void removeLock() {
    if (lockedBy != null) {
      lockedBy = null;
      Gdx.app.debug(TAG, name + " unlocked.");
    }
  }

  public boolean isCompleted() {
    return completed;
  }
}
