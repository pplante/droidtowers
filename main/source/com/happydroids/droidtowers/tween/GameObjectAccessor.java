/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.happydroids.droidtowers.entities.GameObject;

public class GameObjectAccessor implements TweenAccessor<GameObject> {
  public static final int POSITION = 1;
  public static final int POSITION_X = 2;
  public static final int POSITION_Y = 3;
  public static final int OPACITY = 4;
  public static final int TEXTURE_UV = 5;
  public static final int TEXTURE_VV2 = 6;
  public static final int COLOR = 7;

  public int getValues(GameObject target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case COLOR:
        Color color = target.getColor();
        returnValues[0] = color.r;
        returnValues[1] = color.g;
        returnValues[2] = color.b;
        returnValues[3] = color.a;
        return 4;

      case OPACITY:
        returnValues[0] = target.getOpacity();
        return 1;

      case POSITION:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;

      case POSITION_X:
        returnValues[0] = target.getX();
        return 1;

      case POSITION_Y:
        returnValues[0] = target.getY();
        return 1;

      case TEXTURE_UV:
        returnValues[0] = target.getU();
        returnValues[1] = target.getV();
        return 2;

      case TEXTURE_VV2:
        returnValues[0] = target.getV();
        returnValues[1] = target.getV2();
        return 2;

      default:
        assert false;
        return 0;
    }
  }

  public void setValues(GameObject target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case COLOR:
        target.setColor(newValues[0], newValues[1], newValues[2], newValues[3]);
        break;

      case OPACITY:
        target.setOpacity(newValues[0]);
        break;

      case POSITION:
        target.setPosition(newValues[0], newValues[1]);
        break;

      case POSITION_X:
        target.setX(newValues[0]);
        break;

      case POSITION_Y:
        target.setY(newValues[0]);
        break;

      case TEXTURE_UV:
        target.setU(newValues[0]);
        target.setV(newValues[1]);
        break;

      case TEXTURE_VV2:
        target.setV(newValues[0]);
        target.setV2(newValues[1]);
        break;

      default:
        assert false;
        break;
    }
  }
}
