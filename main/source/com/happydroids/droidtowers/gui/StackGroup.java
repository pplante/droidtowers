/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Layout;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.LibgdxToolkit;

public class StackGroup extends WidgetGroup {
  private int padding;

  public float getPrefWidth() {
    float width = 0;
    for (Actor aChildren : children) {
      width += LibgdxToolkit.instance.getPrefWidth(aChildren);
    }

    return width * scaleX;
  }

  public float getPrefHeight() {
    float height = 0;
    for (Actor aChildren : children) {
      height += LibgdxToolkit.instance.getPrefHeight(aChildren);
    }

    return height * scaleY;
  }

  public void layout() {
    float nextY = padding;

    for (Actor child : children) {
      child.x = padding;
      child.y = nextY;

      if (child instanceof Layout) {
        Layout layout = (Layout) child;
        layout.invalidate();
        layout.validate();
      }

      nextY += child.height + padding;
    }
  }

  public void pad(int padding) {
    this.padding = padding;
  }
}
