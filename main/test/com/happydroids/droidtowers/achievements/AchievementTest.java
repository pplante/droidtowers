/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;


import com.happydroids.droidtowers.NonGLTestRunner;
import com.happydroids.droidtowers.types.ServiceRoomTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(NonGLTestRunner.class)
public class AchievementTest {
  @Before
  public void setUp() {
    ServiceRoomTypeFactory.instance();
  }

  @Test
  public void toRewardString_shouldOutputProperMessageForGives() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new Reward(RewardType.GIVE, AchievementThing.MONEY, 100));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nAwarded $100");
  }

  @Test
  public void toRewardString_shouldOutputProperMessageForUnlocks() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new Reward(RewardType.UNLOCK, AchievementThing.OBJECT_TYPE, "MAIDS_CLOSET"));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nUnlocked Maids Closet");
  }

  @Test
  public void toRewardString_shouldHandleMultipleRewards() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new Reward(RewardType.GIVE, AchievementThing.MONEY, 100));
    achievement.addReward(new Reward(RewardType.UNLOCK, AchievementThing.OBJECT_TYPE, "MAIDS_CLOSET"));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nAwarded $100\nUnlocked Maids Closet");
  }
}
