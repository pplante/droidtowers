/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class WidgetAccessor implements TweenAccessor<Actor> {
  public static final int POSITION = 1;
  public static final int OPACITY = 2;
  public static final int SCALE = 3;
  public static final int ROTATION = 4;
  public static final int SIZE = 5;
  public static final int COLOR = 6;

  public int getValues(Actor target, int tweenType, float[] returnValues) {
    Color color = target.color;
    switch (tweenType) {
      case COLOR:
        if (target instanceof Label) {
          color = ((Label) target).getColor();
        }

        returnValues[0] = color.r;
        returnValues[1] = color.g;
        returnValues[2] = color.b;
        returnValues[3] = color.a;
        return 4;

      case OPACITY:
        returnValues[0] = color.a;
        return 1;

      case POSITION:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        return 2;

      case ROTATION:
        returnValues[0] = target.rotation;
        return 1;

      case SCALE:
        returnValues[0] = target.scaleX;
        returnValues[1] = target.scaleY;
        return 2;

      case SIZE:
        returnValues[0] = target.width;
        returnValues[1] = target.height;
        return 2;

      default:
        assert false;
        return 0;
    }
  }

  public void setValues(Actor target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case COLOR:
        if (target instanceof Label) {
          ((Label) target).setColor(newValues[0], newValues[1], newValues[2], newValues[3]);
        } else {
          target.color.set(newValues[0], newValues[1], newValues[2], newValues[3]);
        }
        break;

      case OPACITY:
        target.color.a = newValues[0];
        break;

      case POSITION:
        target.x = newValues[0];
        target.y = newValues[1];
        break;

      case ROTATION:
        target.rotation = newValues[0];
        break;

      case SCALE:
        target.scaleX = newValues[0];
        target.scaleY = newValues[1];
        break;

      case SIZE:
        target.x += target.width - newValues[0];
        target.y += target.height - newValues[1];
        target.width = newValues[0];
        target.height = newValues[1];
        break;

      default:
        assert false;
        break;
    }
  }
}
