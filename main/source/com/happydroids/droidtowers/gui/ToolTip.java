/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.graphics.PixmapGenerator;

public class ToolTip extends Table {
  private final Label label;
  private final PixmapGenerator pixmapGenerator;

  public ToolTip(Skin skin) {
    pixmapGenerator = new PixmapGenerator() {
      @Override
      protected Pixmap generate() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(0, 0, 0, 0.65f));
        pixmap.fill();
        return pixmap;
      }
    };
    visible = false;
    label = new Label(skin);

    defaults();
    setBackground(pixmapGenerator.getNinePatch());
    pad(4);
    add(label);
    pack();
  }

  public void setText(String message) {
    label.setText(message);
    pack();
  }

  @Override
  public void remove() {
    pixmapGenerator.dispose();

    super.remove();
  }
}
