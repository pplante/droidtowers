/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.platform.Display.scale;

public class DebugWindow extends ScrollableTowerWindow {
  public DebugWindow(Stage stage) {
    super("Debug", stage);

    defaults().pad(scale(10));

    row();
    add(makeResetAchievementsButton());

    row();
    add(makeGiveMoneyButton());

    shoveContentUp();
  }

  private TextButton makeGiveMoneyButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Give $100K");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Player.instance().addCurrency(100000);
      }
    });
    return button;
  }

  private TextButton makeResetAchievementsButton() {
    TextButton resetAchievements = FontManager.Roboto24.makeTextButton("Reset Achievements");
    resetAchievements.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        AchievementEngine.instance().resetState();
        ((TowerScene) SceneManager.getActiveScene()).getGameState().saveGame(true);
      }
    });
    return resetAchievements;
  }
}
