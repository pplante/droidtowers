/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.audio.GameSoundController;

import static com.badlogic.gdx.graphics.Color.WHITE;
import static com.happydroids.droidtowers.Colors.ICS_BLUE;

public class AudioControl extends ImageButton {
  public AudioControl(TextureAtlas hudAtlas) {
    super(new NinePatchDrawable(new NinePatch(hudAtlas.findRegion("audio-on"), WHITE)),
                 new NinePatchDrawable(new NinePatch(hudAtlas.findRegion("audio-on"), ICS_BLUE)),
                 new NinePatchDrawable(new NinePatch(hudAtlas.findRegion("audio-off"), WHITE)));
    layout();

    GameSoundController.runAfterInit(new Runnable() {
      @Override
      public void run() {
        setChecked(!DroidTowersGame.getSoundController().isAudioState());

        addListener(new VibrateClickListener() {
          public void onClick(InputEvent event, float x, float y) {
            DroidTowersGame.getSoundController().toggleAudio();
          }
        });
      }
    });
  }
}
