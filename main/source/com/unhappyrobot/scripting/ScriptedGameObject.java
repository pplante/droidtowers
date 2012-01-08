package com.unhappyrobot.scripting;

import com.badlogic.gdx.math.Vector2;
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
