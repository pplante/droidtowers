/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.grid.GameGridRenderer;

import static com.happydroids.droidtowers.TowerAssetManager.texture;
import static com.happydroids.droidtowers.platform.Display.scale;

class DataOverlayLayer extends PopOverLayer {
  private final GameGridRenderer gameGridRenderer;

  DataOverlayLayer(final GameGridRenderer gameGridRenderer) {
    this.gameGridRenderer = gameGridRenderer;
    alignArrow(Align.RIGHT);
  }

  @Override
  protected void show(Actor parentWidget, Actor relativeTo) {
    content.clear();
    buildControls();

    super.show(parentWidget, relativeTo);
  }

  private void buildControls() {
    boolean unlockedJanitors = AchievementEngine.instance().findById("build5commercialspaces").hasGivenReward();
    boolean unlockedMaids = AchievementEngine.instance().findById("build8hotelroom").hasGivenReward();

    for (final Overlays overlay : Overlays.values()) {
      if (overlay.equals(Overlays.DIRT_LEVEL) && (!unlockedJanitors || !unlockedMaids)) {
        continue;
      }

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


      Image colorSwatch = new Image(texture(TowerAssetManager.WHITE_SWATCH), Scaling.stretch);
      colorSwatch.color.set(overlay.getColor(1f));
      colorSwatch.setClickListener(new VibrateClickListener() {
        @Override
        public void onClick(Actor actor, float x, float y) {
          checkBox.click(x, y);
        }
      });

      row().left();
      add(checkBox).pad(0).fillX();
      add(colorSwatch)
              .width(16)
              .height(16);
    }
  }
}
