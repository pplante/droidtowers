package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuItem extends TextButton {
  public MenuItem(String labelText, ClickListener clickListener, Skin skin) {
    super(labelText, skin);

    getLabel().setAlignment(Align.LEFT);
    setText(labelText);
    setClickListener(clickListener);
  }
}
