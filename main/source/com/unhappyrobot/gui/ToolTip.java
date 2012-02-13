package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class ToolTip extends Table {
  private static Pixmap pixmap;
  private static NinePatch background;
  private final Label label;

  public ToolTip() {
    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
      pixmap.setColor(new Color(0, 0, 0, 0.65f));
      pixmap.fill();

      background = new NinePatch(new Texture(pixmap));
    }
    visible = false;
    label = new Label(HeadsUpDisplay.getInstance().getGuiSkin());

    defaults();
    setBackground(background);
    pad(4);
    add(label);
    pack();
  }

  public void setText(String message) {
    label.setText(message);
    pack();
  }
}
