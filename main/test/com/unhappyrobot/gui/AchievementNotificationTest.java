package com.unhappyrobot.gui;

import com.unhappyrobot.TowerGameTestRunner;
import com.unhappyrobot.achievements.Achievement;
import com.unhappyrobot.achievements.AchievementReward;
import com.unhappyrobot.achievements.AchievementThing;
import com.unhappyrobot.achievements.RewardType;
import com.unhappyrobot.tween.TweenSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.Expect.expect;

@RunWith(TowerGameTestRunner.class)
public class AchievementNotificationTest {

  private AchievementNotification notification;

  @Before
  public void setUp() throws Exception {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.GIVE, AchievementThing.MONEY, 100));

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
    expect(TweenSystem.getTweenManager().size()).toEqual(3);

    TweenSystem.getTweenManager().update(1);
    expect(notification.color.a).toEqual(0.005f);
    TweenSystem.getTweenManager().update(300);
    expect(notification.color.a).toEqual(1f);
  }

  @Test
  public void hide_shouldQueueTweenThatModifiesAlpha() {
    notification.hide(false);

    expect(TweenSystem.getTweenManager().containsTarget(notification)).toBeTrue();
    expect(TweenSystem.getTweenManager().size()).toEqual(5);

    expect(notification.color.a).toEqual(1f);
    TweenSystem.getTweenManager().update(300);
    expect(notification.color.a).toEqual(0f);
  }
}
