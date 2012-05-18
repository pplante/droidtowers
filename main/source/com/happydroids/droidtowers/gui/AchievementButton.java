/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;

public class AchievementButton extends ImageButton {
  public AchievementButton(AchievementEngine achievementEngine) {
    super(TowerAssetManager.textureFromAtlas("achievements", "hud/buttons.txt"),
                 TowerAssetManager.textureFromAtlas("achievements", "hud/buttons.txt"),
                 TowerAssetManager.textureFromAtlas("achievements-active", "hud/buttons.txt"));

    setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        new AchievementListView(getStage()).show();
      }
    });

    visible = false;
  }

  @Override
  public void act(float delta) {
    setChecked(AchievementEngine.instance().hasPendingAwards());
  }
}
