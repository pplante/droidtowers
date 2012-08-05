/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.platform.Display;

public class AvatarListWindow extends ScrollableTowerWindow {
  public AvatarListWindow(Stage stage, AvatarLayer avatarLayer) {
    super("Droids", stage);

    defaults().space(Display.devicePixel(8));

    row().fillX().padTop(Display.devicePixel(8));
    add(FontManager.Default.makeLabel("NAME")).expandX();
    add(FontManager.Default.makeLabel("HUNGER")).width(100);
    add(FontManager.Default.makeLabel("HAPPINESS")).width(100);
    add(FontManager.Default.makeLabel("WALKING TO")).width(200);

    row().fillX();
    addHorizontalRule(Colors.ICS_BLUE, 2, 4);

    for (Avatar avatar : avatarLayer.getObjects()) {
      row().fillX();
      add(new AvatarInfoRow(avatar)).expandX().colspan(4);
    }
  }
}
