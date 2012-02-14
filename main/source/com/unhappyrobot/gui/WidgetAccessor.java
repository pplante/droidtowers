package com.unhappyrobot.gui;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class WidgetAccessor implements TweenAccessor<Actor> {
  public static final int POSITION = 1;
  public static final int OPACITY = 2;
  public static final int SCALE = 3;
  public static final int ROTATION = 4;

  public int getValues(Actor target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case OPACITY:
        returnValues[0] = target.color.a;
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

      default:
        assert false;
        return 0;
    }
  }

  public void setValues(Actor target, int tweenType, float[] newValues) {
    switch (tweenType) {
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

      default:
        assert false;
        break;
    }
  }
}
