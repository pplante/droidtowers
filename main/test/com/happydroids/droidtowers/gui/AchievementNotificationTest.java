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

    expect(TweenSystem.getTweenManager().containsTarget(notification)).toBeTrue();

    TweenSystem.getTweenManager().update(1);
    expect(notification.color.a).toEqual(0.005f);
    TweenSystem.getTweenManager().update(300);
    expect(notification.color.a).toEqual(1f);
  }

  @Test
  public void hide_shouldQueueTweenThatModifiesAlpha() {
    notification.hide(false);

    expect(TweenSystem.getTweenManager().containsTarget(notification)).toBeTrue();

    expect(notification.color.a).toEqual(1f);
    TweenSystem.getTweenManager().update(300);
    expect(notification.color.a).toEqual(0f);
  }
}
