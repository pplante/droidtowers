/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.collect.Sets;
import com.unhappyrobot.TowerAssetManager;

import java.util.Set;

public class BubbleGroup extends WidgetGroup {
  private static TextureAtlas textureAtlas;
  private final NinePatch ninePatch;
  private final Image pointer;
  private final Table container;
  private Set<Actor> actors;
  private int directions;

  public BubbleGroup() {
    if (textureAtlas == null) {
      textureAtlas = TowerAssetManager.textureAtlas("hud/misc.txt");
    }

//    visible = false;
    actors = Sets.newHashSet();
    ninePatch = new NinePatch(textureAtlas.findRegion("speech-bubble-box"), 4, 4, 4, 4);
    pointer = new Image(textureAtlas.findRegion("speech-bubble-tip"), Scaling.none);
    container = new Table();

    addActor(container);
  }

  @Override
  public void layout() {
    container.clear();
    container.defaults();
    container.setBackground(ninePatch);
    container.pad(4);

    conditionallyAddPointer(Align.TOP);
    conditionallyAddPointer(Align.LEFT);

    for (Actor child : actors) {
      container.add(child);
    }

    conditionallyAddPointer(Align.RIGHT);
    conditionallyAddPointer(Align.BOTTOM);

    container.pack();
  }

  private void conditionallyAddPointer(int alignmentToCheck) {
    if ((directions & alignmentToCheck) == 0 || container.getActors().contains(pointer)) {
      return;
    }

    switch (alignmentToCheck) {
      case Align.TOP:
        container.add(pointer).align(directions).padTop(-18);
        pointer.rotation = 180f;
        container.row();
        break;
      case Align.LEFT:
        pointer.rotation = 270f;
        container.add(pointer).align(directions).padLeft(-6);
        break;
      case Align.RIGHT:
        pointer.rotation = 90f;
        container.add(pointer).align(directions).padRight(-22);
        break;
      case Align.BOTTOM:
        container.row().align(directions).padBottom(-9);
        pointer.rotation = 0f;
        container.add(pointer);
        break;
    }
  }

  public float getPrefWidth() {
    return 0;
  }

  public float getPrefHeight() {
    return 0;
  }

  public void add(Actor actor) {
    actors.add(actor);
  }

  public void setDirection(int directions) {
    this.directions = directions;
  }
}
