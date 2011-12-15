package com.unhappyrobot.scripting;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.DeferredManager;
import com.unhappyrobot.Game;
import com.unhappyrobot.entities.GameObject;
import org.mozilla.javascript.ScriptableObject;

public class ScriptedGameObject extends ScriptableObject {
  public GameObject getGameObject() {
    return gameObject;
  }

  private GameObject gameObject;

  public ScriptedGameObject() {
    gameObject = new GameObject(0.0f, 0.0f);
  }

  public void jsFunction_useTexture(final String filename) {
    // this looks weird, but its so we can force texture loading onto the correct
    // thread.  otherwise there is a concurrent modification exception and the
    // entire world comes crashing down on us.
    DeferredManager.onGameThread().runOnce(new Runnable() {
      public void run() {
        gameObject.useTexture(filename);
        Game.getLayers().get(0).addChild(gameObject);
      }
    });
  }

  public void jsFunction_setPosition(double x, double y) {
    gameObject.setPosition((float) x, (float) y);
  }

  public Vector2 jsFunction_getPosition() {
    return gameObject.getPosition();
  }

  public void jsFunction_setScale(double scale) {
    gameObject.setScale((float) scale);
  }

  @Override
  public String getClassName() {
    return "ScriptedGameObject";
  }
}
