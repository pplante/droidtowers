package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.unhappyrobot.entities.GameGrid;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  public static class Keys extends Input.Keys {
  }

  private OrthographicCamera camera;

  private CameraController cameraController;
  private GestureDetector gestureDetector;
  private final GestureDelegater delegater;
  private HashMap<Integer, ArrayList<Action>> keyBindings;

  public InputSystem(OrthographicCamera orthographicCamera, GameGrid gameGrid) {
    keyBindings = new HashMap<Integer, ArrayList<Action>>();
    camera = orthographicCamera;
    delegater = new GestureDelegater(camera, gameGrid);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, delegater);
  }


  public void switchTool(GestureTool selectedTool) {
    delegater.switchTool(selectedTool);
  }

  public GestureListener getCurrentTool() {
    return delegater.getCurrentTool();
  }

  public void bind(int keyCode, Action action) {
    ArrayList<Action> actions = getBindingsForKeyCode(keyCode);

    actions.clear();
    actions.add(action);
  }

  public void listen(int keyCode, Action action) {
    getBindingsForKeyCode(keyCode).add(action);
  }

  private ArrayList<Action> getBindingsForKeyCode(int keyCode) {
    ArrayList<Action> actions;

    if (!keyBindings.containsKey(keyCode)) {
      actions = new ArrayList<Action>();
      keyBindings.put(keyCode, actions);
    } else {
      actions = keyBindings.get(keyCode);
    }

    return actions;
  }

  public boolean keyDown(int keycode) {
    float deltaTime = Gdx.graphics.getDeltaTime();

    if (keyBindings.containsKey(keycode)) {
      ArrayList<Action> actions = keyBindings.get(keycode);
      for (Action action : actions) {
        action.run(deltaTime);
      }

      return true;
    }

    return false;
  }

  public boolean touchDown(int x, int y, int pointer, int button) {
    return gestureDetector.touchDown(x, y, pointer, button);
  }

  public boolean touchUp(int x, int y, int pointer, int button) {
    return gestureDetector.touchUp(x, y, pointer, button);
  }

  public boolean touchDragged(int x, int y, int pointer) {
    return gestureDetector.touchDragged(x, y, pointer);
  }

  public boolean touchMoved(int x, int y) {
    return gestureDetector.touchMoved(x, y);
  }

  public boolean scrolled(int amount) {
    return delegater.getCameraController().scrolled(amount);
  }

  public void update(float deltaTime) {
    delegater.update(deltaTime);
  }
}
