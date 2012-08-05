/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class StackGroup extends WidgetGroup {
  private int padding;

  public float getPrefWidth() {
    float width = 0;
    for (Actor aChildren : getChildren()) {
      width += aChildren.getWidth();
    }

    return width * getScaleX();
  }

  public float getPrefHeight() {
    float height = 0;
    for (Actor aChildren : getChildren()) {
      height += aChildren.getHeight();
    }

    return height * getScaleY();
  }

  public void layout() {
    float nextY = padding;

    for (Actor child : getChildren()) {
      child.setX(padding);
      child.setY(nextY);

      if (child instanceof Layout) {
        Layout layout = (Layout) child;
        layout.invalidate();
        layout.validate();
      }

      nextY += child.getHeight() + padding;
    }
  }

  public void pad(int padding) {
    this.padding = padding;
  }
}
