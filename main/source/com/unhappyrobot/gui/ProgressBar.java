package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ProgressBar extends WidgetGroup {
  private Label statusLabel;
  private float value;

  public ProgressBar() {
    Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444);
    pixmap.setColor(new Color(0, 0, 0, 0.65f));
    pixmap.fill();

    visible = false;
    statusLabel = LabelStyle.BankGothic64.makeLabel("asdf");

//    defaults();
//    setBackground(new NinePatch(new Texture(pixmap)));
//    pad(4);
//    add(statusLabel);
    addActor(statusLabel);
  }

  public void setValue(float value) {
    this.value = value;

    String progressText = String.format("Progress %.1f%%", (value * 100f));
    statusLabel.setText(progressText);
    pack();
  }

  public float getPrefWidth() {
    return 0;
  }

  public float getPrefHeight() {
    return 0;
  }
}
