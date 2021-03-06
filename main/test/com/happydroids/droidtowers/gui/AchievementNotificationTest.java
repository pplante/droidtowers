/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.happydroids.droidtowers.TowerGameTestRunner;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementThing;
import com.happydroids.droidtowers.achievements.Reward;
import com.happydroids.droidtowers.achievements.RewardType;
import com.happydroids.droidtowers.tween.TweenSystem;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(TowerGameTestRunner.class)
@Ignore
public class AchievementNotificationTest {

  private AchievementNotification notification;

  @Before
  public void setUp() throws Exception {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new Reward(RewardType.GIVE, AchievementThing.MONEY, 100));

    notification = new AchievementNotification(achievement);
  }

  @Test
  public void shouldCreateALayoutUsingAchievementMetadata() {
    expect(notification).toHaveChildren(2);
    expect(notification).toHaveLabelWithText("Sample");
    expect(notification).toHaveLabelWithText("Complete: Sample!\nGIVE MONEY 100.0");
  }

  @Test
  public void show_shouldQueueTweenThatModifiesAlpha() {
    notification.show();

    expect(TweenSystem.manager().containsTarget(notification)).toBeTrue();

    TweenSystem.manager().update(1);
    expect(notification.getColor().a).toEqual(0.005f);
    TweenSystem.manager().update(300);
    expect(notification.getColor().a).toEqual(1f);
  }

  @Test
  public void hide_shouldQueueTweenThatModifiesAlpha() {
    notification.dismiss();

    expect(TweenSystem.manager().containsTarget(notification)).toBeTrue();

    expect(notification.getColor().a).toEqual(1f);
    TweenSystem.manager().update(300);
    expect(notification.getColor().a).toEqual(0f);
  }
}
