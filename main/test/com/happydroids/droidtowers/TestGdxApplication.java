/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.badlogic.gdx.utils.Clipboard;

public class TestGdxApplication implements Application {

  private GdxTestPreferences preferences;

  public TestGdxApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
    preferences = new GdxTestPreferences();
  }

    @Override
    public ApplicationListener getApplicationListener() {
        return null;
    }

    public LwjglGraphics getGraphics() {
    return null;
  }

  public Audio getAudio() {
    return null;
  }

  public Input getInput() {
    return null;
  }

  public Files getFiles() {
    return null;
  }

  @Override public Net getNet() {
    return null;
  }

  public void log(String tag, String message) {
  }

    @Override
    public void log(String tag, String message, Throwable exception) {

    }

    public void log(String tag, String message, Exception exception) {
  }

  public void error(String tag, String message) {
  }

  public void error(String tag, String message, Throwable exception) {
  }

  public void debug(String tag, String message) {
  }

  public void debug(String tag, String message, Throwable exception) {
  }

  public void setLogLevel(int logLevel) {
  }

    @Override
    public int getLogLevel() {
        return 0;
    }

    public ApplicationType getType() {
    return null;
  }

  public int getVersion() {
    return 0;
  }

  public long getJavaHeap() {
    return 0;
  }

  public long getNativeHeap() {
    return 0;
  }

  public Preferences getPreferences(String name) {
    return preferences;
  }

  @Override public Clipboard getClipboard() {
    return null;
  }

  public void postRunnable(Runnable runnable) {
  }

  public void exit() {
  }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }
}
