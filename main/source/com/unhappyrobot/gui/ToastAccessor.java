package com.unhappyrobot.gui;

import aurelienribon.tweenengine.TweenAccessor;

public class ToastAccessor implements TweenAccessor<Toast> {
  public static final int OPACITY = 2;

  public int getValues(Toast target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case ToastAccessor.OPACITY:
        returnValues[0] = target.alpha;
        return 1;
    }

    return 0;
  }

  public void setValues(Toast target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case ToastAccessor.OPACITY:
        target.alpha = newValues[0];
        break;
    }
  }
}
