package com.unhappyrobot.achievements;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Achievement {
  private String id;
  private String name;
  private Set<AchievementRequirement> requirements;
  private Set<AchievementReward> rewards;
  private boolean completed;
  private boolean gaveRewards;

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
      StringBuilder summary = new StringBuilder();
      for (AchievementReward reward : rewards) {
        reward.give();

        summary.append(reward.getType());
        summary.append(" ");
        summary.append(reward.getThing());
        summary.append("\n");
      }


      return String.format("Complete: %s!\n%s", name, summary);
    }

    return null;
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
}
