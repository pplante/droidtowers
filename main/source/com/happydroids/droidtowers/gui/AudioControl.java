/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.happydroids.droidtowers.TowerGame;

public class AudioControl extends ImageButton {
  public AudioControl(TextureAtlas hudAtlas) {
    super(hudAtlas.findRegion("audio-on"), null, hudAtlas.findRegion("audio-off"));

    setChecked(!TowerGame.getSoundController().isAudioState());

    setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        TowerGame.getSoundController().toggleAudio();
      }
    });
  }
}
