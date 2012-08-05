/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.platform.Platform;

class GridObjectTypeLockedClickListener extends VibrateClickListener {
  private final GridObjectType gridObjectType;

  public GridObjectTypeLockedClickListener(GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;
  }

  @Override
  public void onClick(InputEvent event, float x, float y) {
    if (gridObjectType.requiresUnlimitedVersion() && !Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
      showLockedByUnlimitedVersionDialog();
      return;
    }

    for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
      if (achievement.getRewards().contains(gridObjectType.getLock())) {
        showLockedByAchievementDialog(achievement);
        return;
      }
    }
  }

  private void showLockedByUnlimitedVersionDialog() {
    new PurchaseDroidTowersUnlimitedPrompt().show();
  }

  private void showLockedByAchievementDialog(final Achievement lockedBy) {
    new Dialog()
            .setTitle("Item is Locked!")
            .setMessage("Sorry, this item is locked.\n\nYou may unlock it by completing this achievement:\n" + lockedBy.getName())
            .addButton("Dismiss", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
              }
            })
            .addButton("View Achievement", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
                new AchievementDetailView(lockedBy, SceneManager.activeScene().getStage()).show();
              }
            })
            .show();
  }
}
