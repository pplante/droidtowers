/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import aurelienribon.tweenengine.TweenAccessor;

public class CameraControllerAccessor implements TweenAccessor<CameraController> {
  public static final int PAN = 1;

  public int getValues(CameraController target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case PAN:
        returnValues[0] = target.getCamera().position.x;
        returnValues[1] = target.getCamera().position.y;
        return 2;
    }

    return 0;
  }

  public void setValues(CameraController target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case PAN:
        target.getCamera().position.set(newValues[0], newValues[1], 0f);
        target.checkBounds();
        break;
    }
  }
}
