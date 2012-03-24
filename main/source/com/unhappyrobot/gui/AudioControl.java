package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.unhappyrobot.TowerGame;

public class AudioControl extends ImageButton {
  public AudioControl(TextureAtlas hudAtlas) {
    super(hudAtlas.findRegion("audio-on"), null, hudAtlas.findRegion("audio-off"));

    setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.setAudioEnabled(!TowerGame.isAudioEnabled());
      }
    });
  }
}
