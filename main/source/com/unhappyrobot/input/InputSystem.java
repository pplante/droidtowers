package com.unhappyrobot.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class InputSystem extends InputAdapter {
  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;

  public InputSystem(OrthographicCamera orthographicCamera, Vector2 gameGridPixelSize) {
    camera = orthographicCamera;
    cameraController = new CameraController(camera, gameGridPixelSize);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, cameraController);
  }

  public boolean keyDown(int keycode) {
    return false;
  }

  public boolean keyUp(int keycode) {
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
    return gestureDetector.scrolled(amount);
  }

  public void update(float deltaTime) {
    cameraController.update(deltaTime);
  }
}
