/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class ScrollableTowerWindow extends TowerWindow {
  public ScrollableTowerWindow(String title, Stage stage) {
    super(title, stage);
  }

  @Override
  protected Actor makeContentContainer() {
    ScrollPane scrollPane = new ScrollPane(super.makeContentContainer());
    scrollPane.setFlickScroll(true);
    scrollPane.setFlingTime(0.75f);
    return scrollPane;
  }

  protected void shoveContentUp() {
    shoveContentUp(1);
  }

  protected void shoveContentUp(int colSpan) {
    row().fill().space(0);
    add().expand().colspan(colSpan);
  }
}
