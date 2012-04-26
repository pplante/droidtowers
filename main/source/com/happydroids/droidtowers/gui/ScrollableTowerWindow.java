/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScrollableTowerWindow extends TowerWindow {
  public ScrollableTowerWindow(String title, Stage stage, Skin skin) {
    super(title, stage, skin);
  }

  @Override
  protected Actor makeContentContainer() {
    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(super.makeContentContainer());

    return scrollPane;
  }

  protected void shoveContentUp() {
    row().fill().space(0);
    add().expand();
  }
}
