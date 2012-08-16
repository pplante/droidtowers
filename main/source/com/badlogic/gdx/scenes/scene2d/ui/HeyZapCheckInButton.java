package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.platform.PlatformCheckInManager;
import com.happydroids.platform.Platform;

public class HeyZapCheckInButton extends ImageButton {
  private String message;

  public HeyZapCheckInButton() {
    super(TowerAssetManager.drawableFromAtlas("heyzap-checkin", "hud/heyzap-checkin.txt"), TowerAssetManager.drawableFromAtlas("heyzap-checkin-down", "hud/heyzap-checkin.txt"));

    pad(Display.devicePixel(4));

    addListener(new VibrateClickListener() {
      @Override public void onClick(InputEvent event, float x, float y) {
        PlatformCheckInManager checkInManager = Platform.getCheckInManager();
        if (checkInManager != null) {
          if (message != null) {
            checkInManager.checkInNow(message);
          } else {
            checkInManager.checkInNow();
          }
        }
      }
    });
  }

  public HeyZapCheckInButton(String message) {
    this();
    this.message = message;
  }
}
