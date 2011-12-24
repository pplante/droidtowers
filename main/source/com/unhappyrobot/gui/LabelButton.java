package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelButton extends Button {

  private final Label label;

  public LabelButton(Skin uiSkin, String labelText) {
    super(uiSkin);

    label = new Label(labelText, uiSkin);
    add(label).fill();
  }

  public void setText(String newText) {
    label.setText(newText);
  }
}
