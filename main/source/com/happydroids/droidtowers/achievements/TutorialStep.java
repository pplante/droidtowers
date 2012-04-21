/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class TutorialStep extends Achievement {
  public TutorialStep() {
    super();
  }

  @Override
  public String toRewardString() {
    List<String> summary = Lists.newArrayList();

    if (rewards != null) {
      for (AchievementReward reward : rewards) {
        summary.add(reward.getRewardString());
      }
    }

    return Joiner.on("\n").join(summary);
  }
}
