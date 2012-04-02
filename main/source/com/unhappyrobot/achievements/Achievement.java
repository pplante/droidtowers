package com.unhappyrobot.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.Sets;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Achievement {
  private String id;
  private String name;
  private Set<AchievementRequirement> requirements;
  private Set<AchievementReward> rewards;
  private boolean completed;
  private boolean gaveRewards;

  public Achievement() {

  }

  public Achievement(String name) {
    this.name = name;
    rewards = Sets.newHashSet();
    requirements = Sets.newHashSet();
  }

  public boolean isCompleted() {
    for (AchievementRequirement requirement : requirements) {
      if (!requirement.isCompleted()) {
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
    if (!gaveRewards) {
      gaveRewards = true;
      return toRewardString();
    }

    return null;
  }

  public String toRewardString() {
    StringBuilder summary = new StringBuilder();
    for (AchievementReward reward : rewards) {
      reward.give();
      summary.append(reward.getFormattedString());
    }

    return String.format("Complete: %s!\n%s", name, summary);
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
}
