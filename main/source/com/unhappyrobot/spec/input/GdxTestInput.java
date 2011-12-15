package com.unhappyrobot.spec.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GdxTestInput implements Input {
  public float getAccelerometerX() {
    return 0;
  }

  public float getAccelerometerY() {
    return 0;
  }

  public float getAccelerometerZ() {
    return 0;
  }

  public int getX() {
    return 0;
  }

  public int getX(int pointer) {
    return 0;
  }

  public int getDeltaX() {
    return 0;
  }

  public int getDeltaX(int pointer) {
    return 0;
  }

  public int getY() {
    return 0;
  }

  public int getY(int pointer) {
    return 0;
  }

  public int getDeltaY() {
    return 0;
  }

  public int getDeltaY(int pointer) {
    return 0;
  }

  public boolean isTouched() {
    return false;
  }

  public boolean justTouched() {
    return false;
  }

  public boolean isTouched(int pointer) {
    return false;
  }

  public boolean isButtonPressed(int button) {
    return false;
  }

  public boolean isKeyPressed(int key) {
    return false;
  }

  public void getTextInput(TextInputListener listener, String title, String text) {
  }

  public void setOnscreenKeyboardVisible(boolean visible) {
  }

  public void vibrate(int milliseconds) {
  }

  public void vibrate(long[] pattern, int repeat) {
  }

  public void cancelVibrate() {
  }

  public float getAzimuth() {
    return 0;
  }

  public float getPitch() {
    return 0;
  }

  public float getRoll() {
    return 0;
  }

  public long getCurrentEventTime() {
    return 0;
  }

  public void setCatchBackKey(boolean catchBack) {
  }

  public void setCatchMenuKey(boolean catchMenu) {
  }

  public void setInputProcessor(InputProcessor processor) {
  }

  public InputProcessor getInputProcessor() {
    return null;
  }

  public boolean isPeripheralAvailable(Peripheral peripheral) {
    return false;
  }

  public int getRotation() {
    return 0;
  }

  public Orientation getNativeOrientation() {
    return null;
  }

  public void setCursorCatched(boolean catched) {
  }

  public boolean isCursorCatched() {
    return false;
  }

  public void setCursorPosition(int x, int y) {
  }
}
