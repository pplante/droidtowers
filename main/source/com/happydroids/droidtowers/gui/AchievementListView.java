/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;

import javax.annotation.Nullable;
import java.util.List;


public class AchievementListView extends ScrollableTowerWindow {
  private Drawable itemSelectBackground;

  public AchievementListView(Stage stage) {
    super("Achievements", stage);

    itemSelectBackground = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Colors.ICS_BLUE);

    defaults();

    List<Achievement> achievements = AchievementEngine.instance().getAchievements();
    List<Achievement> sortedAchievements = Ordering.natural().onResultOf(new Function<Achievement, Comparable>() {
      @Override
      public Comparable apply(@Nullable Achievement achievement) {
        if (achievement.isCompleted()) {
          return 50;
        } else if (achievement.isLocked()) {
          return 100;
        }

        return 0;
      }
    }).sortedCopy(achievements);

    for (Achievement achievement : sortedAchievements) {
      makeItem(achievement);
    }

    shoveContentUp();
  }

  public void makeItem(final Achievement achievement) {
    row().expandX();
    AchievementListViewItem actor = new AchievementListViewItem(this, achievement, itemSelectBackground);
    actor.addListener(new VibrateClickListener() {
      @Override
      public void onClick(final InputEvent event, float x, float y) {
        if (achievement.isCompleted() && !achievement.hasGivenReward()) {
          dismiss();
          achievement.giveReward();
          AchievementEngine.instance().displayNotification(achievement);
        } else {
          new AchievementDetailView(achievement, stage).show();
        }
      }
    });
    add(actor).fill();
  }
}
