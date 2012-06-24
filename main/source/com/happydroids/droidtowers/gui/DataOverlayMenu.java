/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
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
      checkBox.setClickListener(new VibrateClickListener() {
        public void onClick(Actor actor, float x, float y) {
          if (checkBox.isChecked()) {
            for (Actor otherCheckbox : getActors()) {
              if (otherCheckbox instanceof CheckBox && otherCheckbox != checkBox) {
                ((CheckBox) otherCheckbox).setChecked(false);
              }
            }

            gameGridRenderer.setActiveOverlay(overlay);
          } else {
            gameGridRenderer.setActiveOverlay(null);
          }
        }
      });


      Image actor = new Image(texture(TowerAssetManager.WHITE_SWATCH), Scaling.stretch);
      actor.color.set(overlay.getColor(1f));
      actor.setClickListener(new VibrateClickListener() {
        @Override
        public void onClick(Actor actor, float x, float y) {
          checkBox.click(x, y);
        }
      });

      row().left();
      add(checkBox).pad(0).fillX();
      add(actor)
              .width(16)
              .height(16);
    }
  }
}
