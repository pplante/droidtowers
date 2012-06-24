/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.platform.Display.scale;

public class DebugWindow extends ScrollableTowerWindow {
  public DebugWindow(Stage stage) {
    super("Debug", stage);

    //noinspection PointlessBooleanExpression
    if (!TowerConsts.DEBUG) {
      throw new RuntimeException("ZOMG WUT?");
    }

    defaults().pad(scale(10)).left();

    if (SceneManager.getActiveScene() instanceof TowerScene) {
      row();
      add(makeResetAchievementsButton());
      add(makeCompleteAllAchievementsButton());

      row();
      add(makeGiveMoneyButton());
    }

    row();
    add(makeInvalidateTexturesButton());
    add(makeRestartActiveSceneButton());

    row();
    add(makeDisconnectHappyDroidsButton());

    shoveContentUp();
  }

  private Actor makeDisconnectHappyDroidsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Disconnect from happydroids");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerGameService.instance().setSessionToken(null);
      }
    });
    return button;
  }

  private TextButton makeRestartActiveSceneButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Restart Active Scene");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        SceneManager.restartActiveScene();
      }
    });
    return button;
  }

  private TextButton makeInvalidateTexturesButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Invalidate Textures");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerAssetManager.assetManager().invalidateAllTextures();
      }
    });
    return button;
  }

  private Actor makeCompleteAllAchievementsButton() {
    TextButton button = FontManager.Roboto24.makeTextButton("Complete all achievements");
    button.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        AchievementEngine.instance().completeAll();
        TutorialEngine.instance().completeAll();
      }
    });
    return button;
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
    TextButton resetAchievements = FontManager.Roboto24.makeTextButton("Reset all achievements");
    resetAchievements.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        AchievementEngine.instance().resetState();
        TutorialEngine.instance().resetState();
      }
    });
    return resetAchievements;
  }
}
