package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuItem extends Button {
  public MenuItem(Skin skin, String labelText, ClickListener clickListener) {
    super(skin);

    Label label = new Label(labelText, skin);
    left().add(label);

    setClickListener(clickListener);
  }
}
