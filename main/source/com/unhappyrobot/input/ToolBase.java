package com.unhappyrobot.input;

import com.badlogic.gdx.input.GestureDetector;

public class ToolBase implements GestureDetector.GestureListener {
  public boolean touchDown(int x, int y, int pointer) {
    return false;
  }

  public boolean tap(int x, int y, int count) {
    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    return false;
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return false;
  }

  public void cleanup() {

  }
}
