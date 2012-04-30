/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.grid.GameGridRenderer;

public class OverlayControl extends ImageButton {
  private Table overlayMenu;

  public OverlayControl(TextureAtlas hudAtlas, Skin skin, final GameGridRenderer gameGridRenderer) {
    super(hudAtlas.findRegion("overlay-button"));

    setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        if (overlayMenu.getStage() == null) {
          stage.addActor(overlayMenu);
        }

        overlayMenu.visible = !overlayMenu.visible;
      }
    });
  }

}
