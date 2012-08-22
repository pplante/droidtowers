/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes.components;

import com.badlogic.gdx.Gdx;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.scenes.Scene;

import java.util.LinkedList;

public class SceneManager {
  private static final String TAG = SceneManager.class.getSimpleName();
  private static Scene activeScene;
  private static LinkedList<Scene> pausedScenes = Lists.newLinkedList();

  public static void changeScene(Class<? extends Scene> sceneClass, Object... args) {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("Switching scene to: " + sceneClass.getSimpleName());
    }

    popScene();
    pushScene(sceneClass, args);
  }

  public static void pushScene(Class<? extends Scene> sceneClass, Object... args) {
    try {
      if (activeScene != null) {
        activeScene.pause();
        InputSystem.instance().removeInputProcessor(activeScene.getStage());
        pausedScenes.addFirst(activeScene);
      }

      activeScene = sceneClass.newInstance();
      activeScene.setStartArgs(args);
      activeScene.create(args);
      InputSystem.instance().addInputProcessor(activeScene.getStage(), 10);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void popScene() {
    if (activeScene != null) {
      InputSystem.instance().removeInputProcessor(activeScene.getStage());
      activeScene.pause();
      activeScene.dispose();

      activeScene = null;
    }

    if (!pausedScenes.isEmpty()) {
      activeScene = pausedScenes.poll();
      activeScene.resume();
      InputSystem.instance().addInputProcessor(activeScene.getStage(), 10);
      Gdx.app.error(TAG, "popScene resumed instance of: " + activeScene.getClass().getSimpleName());
    } else {
      Gdx.app.error(TAG, "popScene says there are no more scenes.");
    }
  }

  public static Scene activeScene() {
    return activeScene;
  }

  public static Scene previousScene() {
    if (!pausedScenes.isEmpty()) {
      return pausedScenes.getFirst();
    }

    return null;
  }

  public static void restartActiveScene() {
    if (activeScene != null) {
      changeScene(activeScene.getClass(), activeScene.getStartArgs());
    }
  }

  public static void dispose() {
    activeScene = null;
    pausedScenes = null;
  }
}
