package com.unhappyrobot.achievements;

import com.unhappyrobot.gui.HeadsUpDisplay;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Achievement {
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

  public void giveReward() {
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


      HeadsUpDisplay.getInstance().showToast("Achievement Completed!\n\n%s", summary);
    }
  }
}
