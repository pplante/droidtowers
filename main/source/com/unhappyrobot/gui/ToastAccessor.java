package com.unhappyrobot.gui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ToastAccessor implements TweenAccessor<Toast> {
  static {
    Tween.registerAccessor(Actor.class, new ToastAccessor());
  }

  public static final int POSITION = 1;
  public static final int OPACITY = 2;

  public int getValues(Toast target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case OPACITY:
        returnValues[0] = target.alpha;
        return 1;

      case POSITION:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        return 2;

      default:
        assert false;
        return 0;
    }
  }

  public void setValues(Toast target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case OPACITY:
        target.alpha = newValues[0];
        break;

      case POSITION:
        target.x = newValues[0];
        target.y = newValues[1];
        break;

      default:
        assert false;
        break;
    }
  }
}
