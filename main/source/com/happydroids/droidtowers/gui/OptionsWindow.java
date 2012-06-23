/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;

public class OptionsWindow extends TowerWindow {
  public OptionsWindow(Stage stage) {
    super("Options", stage);

    defaults().left();

    CheckBox soundEffectsCheckbox = FontManager.Roboto18.makeCheckBox("Sound Effects");
    soundEffectsCheckbox.setChecked(TowerGameService.instance().getAudioState());
    soundEffectsCheckbox.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        TowerGame.getSoundController().toggleAudio();
      }
    });

    row();
    add(soundEffectsCheckbox);

    row();
    add(FontManager.Roboto18.makeLabel("Device ID: " + TowerGameService.instance().getDeviceId()));

    row();
    add(FontManager.Roboto18.makeLabel("Game Version: " + HappyDroidConsts.VERSION + " (" + HappyDroidConsts.GIT_SHA.substring(0, 8) + ")"));
  }
}
