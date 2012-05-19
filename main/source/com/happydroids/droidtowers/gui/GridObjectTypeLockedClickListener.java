/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.types.GridObjectType;

class GridObjectTypeLockedClickListener extends VibrateClickListener {
  private final GridObjectType gridObjectType;

  public GridObjectTypeLockedClickListener(GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;
  }

  @Override
  public void onClick(Actor actor, float x, float y) {
    Achievement lockedBy = null;
    for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
      if (achievement.getRewards().contains(gridObjectType.getLock())) {
        lockedBy = achievement;
        break;
      }
    }

    if (lockedBy == null) {
      return;
    }

    final Achievement finalLockedBy = lockedBy;
    new Dialog()
            .setTitle("Item is Locked!")
            .setMessage("Sorry, this item is locked.\n\nYou may unlock it by completing this achievement: " + lockedBy.getName())
            .addButton("View Achievement", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
                new AchievementDetailView(finalLockedBy, TowerGame.getActiveScene().getStage()).show();
              }
            })
            .addButton(ResponseType.NEGATIVE, "Dismiss", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
              }
            })
            .show();
  }
}
