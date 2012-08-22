/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.events.SwitchToolEvent;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  private static final String TAG = InputSystem.class.getSimpleName();
  private Array<InputProcessorEntry> inputProcessors;

  private HashMap<Integer, Array<InputCallback>> keyBindings;

  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;
  private GestureDelegater gestureDelegater;

  private static InputSystem instance;
  private List<GameLayer> gameLayers;
  private EventBus eventBus;
  private float timeUntilProcessorCleanup;
  private float timeSinceDrag;


  public static InputSystem instance() {
    if (instance == null) {
      instance = new InputSystem();
    }

    return instance;
  }

  public InputSystem() {
    inputProcessors = new Array<InputProcessorEntry>(4);
    keyBindings = Maps.newHashMap();
    eventBus = new SafeEventBus(InputSystem.class.getSimpleName());
  }

  public void addInputProcessor(InputProcessor inputProcessor, int priority) {
    inputProcessors.add(new InputProcessorEntry(inputProcessor, priority));
    inputProcessors.sort();
  }

  public void removeInputProcessor(InputProcessor inputProcessor) {
    for (InputProcessorEntry processorEntry : inputProcessors) {
      if (processorEntry.getInputProcessor() == inputProcessor) {
        processorEntry.markForRemoval();
      }
    }
  }

  public void switchTool(GestureTool selectedTool, Runnable switchToolRunnable) {
    if (gestureDelegater != null) {
      gestureDelegater.switchTool(SceneManager.activeScene()
                                          .getCamera(), gestureDelegater.getGameLayers(), selectedTool, switchToolRunnable);
    }

    eventBus.post(new SwitchToolEvent(selectedTool));
  }

  public GestureListener getCurrentTool() {
    return gestureDelegater != null ? gestureDelegater.getCurrentTool() : null;
  }

  public void bind(int keyCode, InputCallback inputCallback) {
    Array<InputCallback> inputCallbacks = getBindingsForKeyCode(keyCode);
    if (inputCallbacks != null) {
      inputCallback.addBoundKey(keyCode);
      inputCallbacks.insert(0, inputCallback);
    }
  }

  public void bind(int[] keyCodes, InputCallback inputCallback) {
    for (int i = 0, keyCodesLength = keyCodes.length; i < keyCodesLength; i++) {
      int keyCode = keyCodes[i];
      bind(keyCode, inputCallback);
    }
  }

  public void listen(int keyCode, InputCallback inputCallback) {
    getBindingsForKeyCode(keyCode).add(inputCallback);
  }

  private Array<InputCallback> getBindingsForKeyCode(int keyCode) {
    Array<InputCallback> inputCallbacks;

    if (keyBindings == null) {
      keyBindings = Maps.newHashMap();
    }

    if (!keyBindings.containsKey(keyCode)) {
      inputCallbacks = new Array<InputCallback>();
      keyBindings.put(keyCode, inputCallbacks);
    } else {
      inputCallbacks = keyBindings.get(keyCode);
    }

    return inputCallbacks;
  }

  @SuppressWarnings("WhileLoopReplaceableByForEach")
  public boolean keyDown(int keycode) {
    if (keyBindings != null && keyBindings.containsKey(keycode)) {
      float deltaTime = Gdx.graphics.getDeltaTime();

      Array<InputCallback> actionsForKeyCode = keyBindings.get(keycode);
      for (int i = 0, actionsForKeyCodeSize = actionsForKeyCode.size; i < actionsForKeyCodeSize; i++) {
        InputCallback inputCallback = actionsForKeyCode.get(i);
        if (inputCallback.run(deltaTime)) {
          return true;
        }
      }
    }

    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().keyDown(keycode)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().keyTyped(character)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().keyUp(keycode)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchDown(int x, int y, int pointer, int button) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().touchDown(x, y, pointer, button)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchUp(int x, int y, int pointer, int button) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().touchUp(x, y, pointer, button)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchDragged(int x, int y, int pointer) {
    timeSinceDrag = 0f;
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().touchDragged(x, y, pointer)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean scrolled(int amount) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().scrolled(amount)) {
          return true;
        }
      }
    }

    return gestureDelegater != null && gestureDelegater.scrolled(amount);
  }

  public void update(float deltaTime) {
    timeSinceDrag += deltaTime;

    timeUntilProcessorCleanup -= deltaTime;
    if (timeUntilProcessorCleanup <= 0) {
      timeUntilProcessorCleanup = 3f;
      Iterables.removeIf(inputProcessors, INPUT_PROCESSOR_CLEANER);
    }

    if (gestureDelegater != null) {
      gestureDelegater.update(deltaTime);
    }
  }

  public void unbind(int keyCode, InputCallback inputCallbackToRemove) {
    if (keyBindings == null || !keyBindings.containsKey(keyCode)) {
      return;
    }

    Iterator<InputCallback> iterator = keyBindings.get(keyCode).iterator();
    while (iterator.hasNext()) {
      InputCallback inputCallback = iterator.next();
      if (inputCallback == inputCallbackToRemove) {
        iterator.remove();
      }
    }
  }

  public void unbind(int[] keyCodes, InputCallback inputCallback) {
    for (int i = 0, keyCodesLength = keyCodes.length; i < keyCodesLength; i++) {
      int keyCode = keyCodes[i];
      unbind(keyCode, inputCallback);
    }
  }

  public void setGestureDelegate(GestureDelegater gestureDelegate) {
    this.gestureDelegater = gestureDelegate;
  }

  public void unfocusAll() {
    Gdx.app.debug(TAG, "unfocusAll()");

    if (gestureDetector != null) {
      gestureDetector.reset();
    }

    for (int i = 0, inputProcessorsSize = inputProcessors.size; i < inputProcessorsSize; i++) {
      InputProcessorEntry entry = inputProcessors.get(i);
      if (entry.getInputProcessor() instanceof Stage) {
        ((Stage) entry.getInputProcessor()).unfocusAll();
      }
    }
  }

  public void unbind(InputCallback... callbacks) {
    if (callbacks == null || callbacks.length == 0) {
      return;
    }

    for (Map.Entry<Integer, Array<InputCallback>> entry : keyBindings.entrySet()) {
      for (InputCallback callback : callbacks) {
        entry.getValue().removeValue(callback, false);
      }
    }
  }

  public EventBus events() {
    return eventBus;
  }

  public boolean wasDragging() {
    Gdx.app.log(TAG, "timeSinceDrag: " + timeSinceDrag);
    return timeSinceDrag < 0.5f;
  }

  public static class Keys extends Input.Keys {

  }

  public static final Predicate<InputProcessorEntry> INPUT_PROCESSOR_CLEANER = new Predicate<InputProcessorEntry>() {
    @Override
    public boolean apply(@Nullable InputProcessorEntry input) {
      return input.isMarkedForRemoval();
    }
  };
}
