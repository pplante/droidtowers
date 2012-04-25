/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;

public class ScrollableTowerWindow extends TowerWindow {

  private final Table scrolledContent;
  private final WheelScrollFlickScrollPane scrollPane;

  public ScrollableTowerWindow(String title, Stage stage, Skin skin) {
    super(title, stage, skin);

    scrolledContent = new Table();
    scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setFillParent(true);
    scrollPane.setWidget(scrolledContent);

    content.add(scrollPane).fill();
  }

  @Override
  public Cell add(Actor actor) {
    return scrolledContent.add(actor);
  }

  @Override
  public Cell row() {
    return scrolledContent.row();
  }

  @Override
  protected void clear() {
    scrolledContent.clear();
  }

  @Override
  protected void debug() {
    scrolledContent.debug();
  }

  @Override
  protected Cell defaults() {
    return scrolledContent.defaults();
  }
}
