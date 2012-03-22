package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.unhappyrobot.scenes.TowerScene;

public class DefaultKeybindings {
  private DefaultKeybindings() {

  }

  public static void initialize(final TowerScene towerScene) {
    InputSystem.instance().bind(new int[]{InputSystem.Keys.PLUS, InputSystem.Keys.UP}, new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.setTimeMultiplier(Math.min(towerScene.getTimeMultiplier() + 0.5f, 4f));
        return true;
      }
    });

    InputSystem.instance().bind(new int[]{InputSystem.Keys.MINUS, InputSystem.Keys.DOWN}, new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.setTimeMultiplier(Math.max(towerScene.getTimeMultiplier() - 0.5f, 0.5f));
        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.G, new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.getGameGridRenderer().toggleGridLines();

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.T, new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.getGameGridRenderer().toggleTransitLines();

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.NUM_0, new InputCallback() {
      public boolean run(float timeDelta) {
        towerScene.getCamera().zoom = 1f;

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.R, new InputCallback() {
      public boolean run(float timeDelta) {
        Texture.invalidateAllTextures(Gdx.app);
        return true;
      }
    });
  }
}
