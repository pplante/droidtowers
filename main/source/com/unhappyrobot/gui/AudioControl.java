package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class AudioControl extends ImageButton {
  public AudioControl(TextureAtlas hudAtlas) {
    super(hudAtlas.findRegion("audio-on"), null, hudAtlas.findRegion("audio-off"));
  }

  @Override
  public void layout() {
    super.layout();
    x = stage.width() - width - 100;
    y = stage.height() - height - 10;
  }
}
