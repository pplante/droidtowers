/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.google.common.collect.Sets;

import java.util.Set;

public abstract class InputCallback {
  private Set<Integer> keysBound;

  public abstract boolean run(float timeDelta);

  protected void unbind() {
    if (keysBound != null) {
      for (Integer integer : keysBound) {
        InputSystem.instance().unbind(integer, this);
      }
    }
  }

  public void addBoundKey(int keyCode) {
    if (keysBound == null) {
      keysBound = Sets.newHashSet();
    }

    keysBound.add(keyCode);
  }
}
