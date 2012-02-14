package com.unhappyrobot.controllers;

import aurelienribon.tweenengine.TweenAccessor;
import com.unhappyrobot.entities.GameObject;

public class GameObjectAccessor implements TweenAccessor<GameObject> {
  public static final int POSITION = 1;
  public static final int POSITION_X = 2;
  public static final int POSITION_Y = 3;
  public static final int OPACITY = 4;

  public int getValues(GameObject target, int tweenType, float[] returnValues) {
    switch (tweenType) {
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

      default:
        assert false;
        return 0;
    }
  }

  public void setValues(GameObject target, int tweenType, float[] newValues) {
    switch (tweenType) {
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

      default:
        assert false;
        break;
    }
  }
}
