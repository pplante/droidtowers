/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.badlogic.gdx.utils.Pool;
import com.happydroids.droidtowers.achievements.Achievement;

@SuppressWarnings("FieldCanBeLocal")
public class AchievementCompletionEvent implements Pool.Poolable {
  private Achievement achievement;

  public AchievementCompletionEvent() {
  }

  public Achievement getAchievement() {
    return achievement;
  }

  public void setAchievement(Achievement achievement) {
    this.achievement = achievement;
  }

  @Override
  public void reset() {
    achievement = null;
  }
}
