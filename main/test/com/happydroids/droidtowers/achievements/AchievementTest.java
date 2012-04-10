/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;


import com.happydroids.sparky.HappyDroidTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(HappyDroidTestRunner.class)
public class AchievementTest {
  @Test
  @Ignore
  public void toRewardString_shouldOutputProperMessageForGives() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.GIVE, AchievementThing.MONEY, 100));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nReceived ¢ 100\n");
  }

  @Test
  @Ignore
  public void toRewardString_shouldOutputProperMessageForUnlocks() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.UNLOCK, AchievementThing.MAIDS_OFFICE));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nUnlocked Maid's Closet\n");
  }

  @Test
  @Ignore
  public void toRewardString_shouldHandleMultipleRewards() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.GIVE, AchievementThing.MONEY, 100));
    achievement.addReward(new AchievementReward(RewardType.UNLOCK, AchievementThing.MAIDS_OFFICE));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nReceived ¢ 100\nUnlocked Maid's Closet\n");
  }
}
