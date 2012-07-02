/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;

public class HeaderButtonBar extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.25f;
  public static final float BUTTON_FADE_DURATION = 0.25f;

  private final ImageButton dataOverlayButton;
  private final DataOverlayMenu dataOverlayMenu;

  public HeaderButtonBar(TextureAtlas hudAtlas, final GameGrid gameGrid) {
    AudioControl audioControl = new AudioControl(hudAtlas);
    dataOverlayButton = TowerAssetManager.imageButton(hudAtlas.findRegion("overlay-button"));

    dataOverlayMenu = new DataOverlayMenu(gameGrid.getRenderer());
    dataOverlayMenu.visible = false;

    defaults().space(6);
    row().right();
    add(audioControl).expandX();
    add(dataOverlayButton).right();

    pack();

    dataOverlayButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        dataOverlayMenu.toggle(HeaderButtonBar.this, dataOverlayButton);
      }
    });
  }
}
