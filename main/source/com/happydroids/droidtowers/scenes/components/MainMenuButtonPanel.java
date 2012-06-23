/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.*;
import com.happydroids.droidtowers.scenes.HappyDroidConnect;

import static com.happydroids.droidtowers.platform.Display.scale;

public class MainMenuButtonPanel extends Table {
  private static final String TAG = MainMenuButtonPanel.class.getSimpleName();
  public static final int BUTTON_WIDTH = scale(280);
  public static final int BUTTON_SPACING = scale(16);

  public MainMenuButtonPanel() {
    super();

    TextButton newGameButton = FontManager.RobotoBold18.makeTextButton("new tower");
    add(newGameButton).fill().maxWidth(BUTTON_WIDTH);
    row().padTop(BUTTON_SPACING);

    TextButton loadGameButton = FontManager.RobotoBold18.makeTextButton("load tower");
    add(loadGameButton).fill().maxWidth(BUTTON_WIDTH);
    row().padTop(BUTTON_SPACING);

    TextButton optionsButton = FontManager.RobotoBold18.makeTextButton("options");
    add(optionsButton).fill().maxWidth(BUTTON_WIDTH);
    row().padTop(BUTTON_SPACING);

    if (TowerConsts.ENABLE_HAPPYDROIDS_CONNECT) {
      final TextButton connectFacebookButton = FontManager.RobotoBold18.makeTextButton("login to happydroids.com");
      connectFacebookButton.visible = false;
      add(connectFacebookButton).fill().maxWidth(BUTTON_WIDTH);
      row().padTop(BUTTON_SPACING);

      TowerGameService.instance().afterAuthentication(new Runnable() {
        public void run() {
          if (!TowerGameService.instance().isAuthenticated()) {
            connectFacebookButton.visible = true;
            connectFacebookButton.setClickListener(new VibrateClickListener() {
              @Override
              public void onClick(Actor actor, float x, float y) {
                SceneManager.pushScene(HappyDroidConnect.class);
              }
            });
            connectFacebookButton.action(FadeIn.$(0.25f));
          }
        }
      });
    }

    TextButton exitGameButton = FontManager.RobotoBold18.makeTextButton("exit");
    add(exitGameButton).fill().maxWidth(BUTTON_WIDTH);
    row();

    newGameButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new NewTowerWindow(getStage()).show();
      }
    });
    loadGameButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new LoadTowerWindow(getStage()).show();
      }
    });

    optionsButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new OptionsWindow(getStage()).show();
      }
    });

    exitGameButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Gdx.app.exit();
      }
    });
  }
}
