package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuItem extends LabelButton {
  public MenuItem(Skin skin, String labelText, ClickListener clickListener) {
    super(skin, labelText);

    getLabel().setAlignment(Align.LEFT);
    setText(labelText);
    setClickListener(clickListener);
  }
}
