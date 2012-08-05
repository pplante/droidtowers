/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;

public class HeaderButtonBar extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.25f;
  public static final float BUTTON_FADE_DURATION = 0.25f;

  private final ImageButton dataOverlayButton;
  private final DataOverlayPopOver dataOverlayPopOverMenu;


  public HeaderButtonBar(TextureAtlas hudAtlas, final GameGrid gameGrid) {
    AudioControl audioControl = new AudioControl(hudAtlas);
    dataOverlayButton = TowerAssetManager.imageButton(hudAtlas.findRegion("overlay-button"));

    dataOverlayPopOverMenu = new DataOverlayPopOver(gameGrid.getRenderer());
    dataOverlayPopOverMenu.setVisible(false);

    defaults().space(6);
    row().right();
    add(audioControl).expandX();
    add(dataOverlayButton).right();

    pack();

    dataOverlayButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dataOverlayPopOverMenu.toggle(HeaderButtonBar.this, dataOverlayButton);
      }
    });
  }
}
