/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.achievements.Achievement;

@SuppressWarnings("FieldCanBeLocal")
public class AchievementCompletionEvent {
  public final Achievement achievement;

  public AchievementCompletionEvent(Achievement achievement) {
    this.achievement = achievement;
  }
}
