/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.scenes.TowerScene;

public class DefaultKeybindings {

  private InputCallback speedUpTime;
  private final TowerScene towerScene;
  private InputCallback slowDownTime;
  private InputCallback toggleGridLines;
  private InputCallback toggleTransitLines;
  private InputCallback resetCameraZoom;

  public DefaultKeybindings(final TowerScene towerScene) {
    this.towerScene = towerScene;

    speedUpTime = new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.setTimeMultiplier(towerScene.getTimeMultiplier() + 0.5f);
        return true;
      }
    };

    slowDownTime = new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.setTimeMultiplier(towerScene.getTimeMultiplier() - 0.5f);
        return true;
      }
    };

    if (HappyDroidConsts.DEBUG) {
      toggleGridLines = new InputCallback() {
        public boolean run(float timeDelta) {
          towerScene.getGameGridRenderer().toggleGridLines();

          return true;
        }
      };

      toggleTransitLines = new InputCallback() {
        public boolean run(float timeDelta) {
          towerScene.getGameGridRenderer().toggleTransitLines();

          return true;
        }
      };

      resetCameraZoom = new InputCallback() {
        public boolean run(float timeDelta) {
          towerScene.getCamera().zoom = 1f;

          return true;
        }
      };
    }
  }

  public void bindKeys() {
    InputSystem.instance().bind(new int[]{InputSystem.Keys.PLUS, InputSystem.Keys.UP}, speedUpTime);
    InputSystem.instance().bind(new int[]{InputSystem.Keys.MINUS, InputSystem.Keys.DOWN}, slowDownTime);

    if (HappyDroidConsts.DEBUG) {
      InputSystem.instance().bind(InputSystem.Keys.G, toggleGridLines);
      InputSystem.instance().bind(InputSystem.Keys.T, toggleTransitLines);
      InputSystem.instance().bind(InputSystem.Keys.NUM_0, resetCameraZoom);
    }
  }

  public void unbindKeys() {
    InputSystem.instance().unbind(speedUpTime, slowDownTime);

    if (HappyDroidConsts.DEBUG) {
      InputSystem.instance().unbind(toggleGridLines, toggleTransitLines, resetCameraZoom);
    }
  }
}
