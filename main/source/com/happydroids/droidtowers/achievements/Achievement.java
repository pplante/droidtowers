/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Achievement {
  private String id;
  private String name;
  private List<AchievementRequirement> requirements;
  private List<AchievementReward> rewards;
  private boolean completed;
  private boolean gaveRewards;

  public Achievement() {

  }

  public Achievement(String name) {
    this.name = name;
    rewards = Lists.newArrayList();
    requirements = Lists.newArrayList();
  }

  public boolean isCompleted(GameGrid gameGrid) {
    for (AchievementRequirement requirement : requirements) {
      if (!requirement.isCompleted(gameGrid)) {
        System.out.println("Requirement unsatisfied: " + requirement);
        return false;
      }
    }

    completed = true;

    return true;
  }

  @Override
  public String toString() {
    return "Achievement{" +
                   "name='" + name + '\'' +
                   ", requirements=" + requirements +
                   ", rewards=" + rewards +
                   '}';
  }

  public String giveReward() {
    if (!gaveRewards && completed) {
      gaveRewards = true;
      for (AchievementReward reward : rewards) {
        reward.give();
      }

      return toRewardString();
    }

    return null;
  }

  public String toRewardString() {
    List<String> summary = Lists.newArrayList(String.format("Complete: %s!", name));
    for (AchievementReward reward : rewards) {
      summary.add(reward.getRewardString());
    }

    return Joiner.on("\n").join(summary);
  }

  public String getId() {
    return id;
  }

  void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public boolean alreadyGaveReward() {
    return gaveRewards;
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
}
