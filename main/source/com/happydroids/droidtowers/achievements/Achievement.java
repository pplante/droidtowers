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
  private List<Requirement> requirements;
  protected List<Reward> rewards;
  private boolean completed;
  private Reward lockedBy;
  private int totalWeight;
  private int finishedWeight;
  private boolean givenReward;

  public Achievement() {

  }

  public Achievement(String name) {
    this.name = name;
    rewards = Lists.newArrayList();
    requirements = Lists.newArrayList();
  }

  public static Achievement findById(String achievementId) {
    Achievement achievement = AchievementEngine.instance().findById(achievementId);
    if (achievement == null) {
      achievement = TutorialEngine.instance().findById(achievementId);
    }

    if (achievement != null) {
      return achievement;
    }

    throw new RuntimeException("Could not find Achievement with id: " + achievementId);
  }

  public void checkRequirements(GameGrid gameGrid) {
    if ((lockedBy != null) || completed) {
      return;
    }

    if (requirements != null) {
      finishedWeight = 0;
      for (int i = 0, requirementsSize = requirements.size(); i < requirementsSize; i++) {
        Requirement requirement = requirements.get(i);
        boolean completed = requirement.validate(gameGrid);
        finishedWeight += requirement.getCurrentWeight();
      }

      completed = finishedWeight >= totalWeight;
    }
  }

  public String giveReward() {
    if (completed) {
      givenReward = true;
      if (rewards != null) {
        for (Reward reward : rewards) {
          reward.give();
        }
      }
    }

    return toRewardString();
  }

  public void resetState() {
    Gdx.app.debug(TAG, "Reset: " + id);
    completed = false;
    givenReward = false;
    if (rewards != null) {
      for (Reward reward : rewards) {
        reward.resetState();
      }
    } else {
      if (TowerConsts.DEBUG) {
        System.out.println("Achievement has no rewards: " + getId());
      }
    }

    totalWeight = 0;
    finishedWeight = 0;
    if (requirements != null) {
      for (Requirement requirement : requirements) {
        totalWeight += requirement.getAmount();
      }
    }
  }

  public String toRewardString() {
    List<String> summary = Lists.newArrayList();

    if (rewards != null) {
      for (Reward reward : rewards) {
        summary.add(reward.getRewardString(true));
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

  public void addReward(Reward reward) {
    rewards.add(reward);
  }

  public String getName() {
    return name;
  }

  public List<Reward> getRewards() {
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

  public void addLock(Reward reward) {
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

  public int getPercentComplete() {
    if (completed) {
      return 100;
    }

    if (totalWeight > 0) {
      return (int) (((float) finishedWeight / (float) totalWeight) * 100);
    }

    return 0;
  }

  public List<Requirement> getRequirements() {
    return requirements;
  }

  public boolean hasGivenReward() {
    return givenReward;
  }

  public void unlockReward() {
    givenReward = true;
    if (rewards != null) {
      for (Reward reward : rewards) {
        reward.unlock();
      }
    }
  }
}
