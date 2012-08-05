/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.grid.GameGridRenderer;
import com.happydroids.droidtowers.platform.Display;

import static com.happydroids.droidtowers.TowerAssetManager.drawable;

class DataOverlayPopOver extends PopOver {
  private final GameGridRenderer gameGridRenderer;

  DataOverlayPopOver(final GameGridRenderer gameGridRenderer) {
    this.gameGridRenderer = gameGridRenderer;
    alignArrow(Align.right);
  }

  @Override
  protected void show(Actor parentWidget, Actor relativeTo) {
    content.clear();
    buildControls();

    super.show(parentWidget, relativeTo);
  }

  @Override
  protected void hide() {
    super.hide();

    gameGridRenderer.setActiveOverlay(null);
  }

  private void buildControls() {
    boolean unlockedJanitors = AchievementEngine.instance().findById("build5commercialspaces").hasGivenReward();
    boolean unlockedMaids = AchievementEngine.instance().findById("build8hotelroom").hasGivenReward();

    for (final Overlays overlay : Overlays.values()) {
      if (overlay.equals(Overlays.DIRT_LEVEL) && (!unlockedJanitors || !unlockedMaids)) {
        continue;
      }

      final CheckBox checkBox = FontManager.Roboto18.makeCheckBox(overlay.toString());
      checkBox.align(Align.left);
      checkBox.getLabelCell().padLeft(0).spaceLeft(Display.devicePixel(8));
      checkBox.addListener(new VibrateClickListener() {
        public void onClick(InputEvent event, float x, float y) {
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


      Image colorSwatch = new Image(drawable(TowerAssetManager.WHITE_SWATCH), Scaling.stretch);
      colorSwatch.setColor(overlay.getColor(1f));

      row().left();
      add(checkBox).pad(0).fillX();
      add(colorSwatch)
              .width(16)
              .height(16);
    }
  }


}
