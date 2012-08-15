package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.platform.Platform;

public class HeyZapCheckInButton extends ImageButton {
  public HeyZapCheckInButton(TextureAtlas buttonAtlas) {
    super(new TextureRegionDrawable(buttonAtlas.findRegion("heyzap-checkin")));

    addListener(new VibrateClickListener() {
      @Override public void onClick(InputEvent event, float x, float y) {
        Platform.getCheckInManager().checkInNow();
      }
    });
  }
}
