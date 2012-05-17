/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.grid.GameGridRenderer;

import static com.happydroids.droidtowers.TowerAssetManager.texture;
import static com.happydroids.droidtowers.platform.Display.scale;

class DataOverlayMenu extends PopOverMenu {
  DataOverlayMenu(final GameGridRenderer gameGridRenderer) {
    alignArrow(Align.RIGHT);

    for (final Overlays overlay : Overlays.values()) {
      final CheckBox checkBox = FontManager.Roboto18.makeCheckBox(overlay.toString());
      checkBox.align(Align.LEFT);
      checkBox.getLabelCell().padLeft(0).spaceLeft(scale(8));
      checkBox.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          if (checkBox.isChecked()) {
            gameGridRenderer.addActiveOverlay(overlay);
          } else {
            gameGridRenderer.removeActiveOverlay(overlay);
          }
        }
      });

      row().left();
      add(checkBox).pad(0).fillX();

      add(new Image(texture("swatches/" + overlay.getSwatchFilename()), Scaling.stretch))
              .width(16)
              .height(16);
    }

    TextButton clearAllButton = FontManager.Roboto18.makeTextButton("Clear All");
    clearAllButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        gameGridRenderer.clearOverlays();

        for (Actor child : getActors()) {
          if (child instanceof CheckBox) {
            ((CheckBox) child).setChecked(false);
          }
        }
      }
    });

    row().colspan(2);
    add(clearAllButton).fillX();
  }

}
