/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

public class TutorialStep extends Achievement {
  protected boolean tapToGiveReward;
  private boolean notificationShown;


  public TutorialStep() {
    super();
  }

  public boolean requiresTapToGiveReward() {
    return tapToGiveReward;
  }

  public void shownNotification() {
    notificationShown = true;
  }

  public boolean hasShownNotification() {
    return notificationShown;
  }

  @Override
  public void resetState() {
    super.resetState();

    notificationShown = false;
  }
}
