/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.grid.GameGridRenderer;

import static com.happydroids.droidtowers.platform.Display.scale;

public class OverlayControl extends ImageButton {
  private Menu overlayMenu;

  public OverlayControl(TextureAtlas hudAtlas, Skin skin, final GameGridRenderer gameGridRenderer) {
    super(hudAtlas.findRegion("overlay-button"));

    setClickListener(new ClickListener() {
      boolean isShowing;

      public void click(Actor actor, float x, float y) {
        overlayMenu.show(OverlayControl.this);
        overlayMenu.x -= overlayMenu.width - OverlayControl.this.width;
      }
    });

    overlayMenu = new Menu(skin);
    overlayMenu.defaults().pad(4);

    for (final Overlays overlay : Overlays.values()) {
      final CheckBox checkBox = FontManager.Roboto18.makeCheckBox(overlay.toString(), skin);
      checkBox.getLabelCell().pad(null).padLeft(scale(6));
      checkBox.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          if (checkBox.isChecked()) {
            gameGridRenderer.addActiveOverlay(overlay);
          } else {
            gameGridRenderer.removeActiveOverlay(overlay);
          }
        }
      });

      overlayMenu.row().left();
      overlayMenu.add(checkBox);

      overlayMenu.add(new Image(TowerAssetManager.texture("swatches/" + overlay.getSwatchFilename()))).width(16).height(16);
    }

    overlayMenu.row().colspan(2).left().pad(6, 2, 2, 2);
    TextButton clearAllButton = FontManager.Roboto18.makeTextButton("Clear All", skin);
    clearAllButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        gameGridRenderer.clearOverlays();

        for (Actor child : overlayMenu.getActors()) {
          if (child instanceof CheckBox) {
            ((CheckBox) child).setChecked(false);
          }
        }
      }
    });

    overlayMenu.add(clearAllButton).fill();

    overlayMenu.pack();
  }
}
