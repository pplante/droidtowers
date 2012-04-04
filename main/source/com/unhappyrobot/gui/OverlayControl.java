/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.unhappyrobot.graphics.Overlays;
import com.unhappyrobot.grid.GameGridRenderer;

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
    overlayMenu.defaults();
    overlayMenu.top().left();

    for (final Overlays overlay : Overlays.values()) {
      final CheckBox checkBox = new CheckBox(overlay.toString(), skin);
      checkBox.align(Align.LEFT);
      checkBox.getLabelCell().pad(4);
      checkBox.invalidate();
      checkBox.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          if (checkBox.isChecked()) {
            gameGridRenderer.addActiveOverlay(overlay);
          } else {
            gameGridRenderer.removeActiveOverlay(overlay);
          }
        }
      });
      overlayMenu.row().left().pad(2, 6, 2, 6);
      overlayMenu.add(checkBox);
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGB565);
      pixmap.setColor(Color.GRAY);
      pixmap.fill();
      pixmap.setColor(overlay.getColor(1f));
      pixmap.fillRectangle(1, 1, 14, 14);

      Image image = new Image(new Texture(pixmap));
      overlayMenu.add(image);
    }

    overlayMenu.row().colspan(2).left().pad(6, 2, 2, 2);
    TextButton clearAllButton = new TextButton("Clear All", skin);
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
