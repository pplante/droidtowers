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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.events.SwitchToolEvent;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import javax.annotation.Nullable;
import java.util.*;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  private static final String TAG = InputSystem.class.getSimpleName();
  private List<InputProcessorEntry> inputProcessors;

  private HashMap<Integer, ArrayList<InputCallback>> keyBindings;

  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;
  private GestureDelegater gestureDelegater;

  private static InputSystem instance;
  private List<GameLayer> gameLayers;
  private EventBus eventBus;
  private float timeUntilProcessorCleanup;

  public static InputSystem instance() {
    if (instance == null) {
      instance = new InputSystem();
    }

    return instance;
  }

  public InputSystem() {
    inputProcessors = Lists.newArrayList();
    keyBindings = Maps.newHashMap();
    eventBus = new SafeEventBus(InputSystem.class.getSimpleName());
  }

  public void addInputProcessor(InputProcessor inputProcessor, int priority) {
    inputProcessors.add(new InputProcessorEntry(inputProcessor, priority));

    Collections.sort(inputProcessors, new Comparator<InputProcessorEntry>() {
      @Override
      public int compare(InputProcessorEntry entryA, InputProcessorEntry entryB) {
        return entryA.getPriority() > entryB.getPriority() ? 1 : -1;
      }
    });
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
      gestureDelegater.switchTool(SceneManager.activeScene().getCamera(), gestureDelegater.getGameLayers(), selectedTool, switchToolRunnable);
    }

    eventBus.post(new SwitchToolEvent(selectedTool));
  }

  public GestureListener getCurrentTool() {
    return gestureDelegater != null ? gestureDelegater.getCurrentTool() : null;
  }

  public void bind(int keyCode, InputCallback inputCallback) {
    ArrayList<InputCallback> inputCallbacks = getBindingsForKeyCode(keyCode);
    if (inputCallbacks != null) {
      inputCallback.addBoundKey(keyCode);
      inputCallbacks.add(0, inputCallback);
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

  private ArrayList<InputCallback> getBindingsForKeyCode(int keyCode) {
    ArrayList<InputCallback> inputCallbacks;

    if (keyBindings == null) {
      keyBindings = Maps.newHashMap();
    }

    if (!keyBindings.containsKey(keyCode)) {
      inputCallbacks = new ArrayList<InputCallback>();
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

      List<InputCallback> actionsForKeyCode = keyBindings.get(keycode);
      for (int i = 0, actionsForKeyCodeSize = actionsForKeyCode.size(); i < actionsForKeyCodeSize; i++) {
        InputCallback inputCallback = actionsForKeyCode.get(i);
        if (inputCallback.run(deltaTime)) {
          return true;
        }
      }
    }

    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().touchUp(x, y, pointer, button)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchDragged(int x, int y, int pointer) {
    if (inputProcessors != null) {
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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
      for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
        InputProcessorEntry entry = inputProcessors.get(i);
        if (!entry.isMarkedForRemoval() && entry.getInputProcessor().scrolled(amount)) {
          return true;
        }
      }
    }

    return gestureDelegater != null && gestureDelegater.scrolled(amount);
  }

  public void update(float deltaTime) {
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

  public void setGestureDelegator(GestureDelegater gestureDelegator) {
    this.gestureDelegater = gestureDelegator;
  }

  public void unfocusAll() {
    Gdx.app.debug(TAG, "unfocusAll()");

    if (gestureDetector != null) {
      gestureDetector.reset();
    }

    for (int i = 0, inputProcessorsSize = inputProcessors.size(); i < inputProcessorsSize; i++) {
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

    HashSet<InputCallback> callbackHashSet = Sets.newHashSet(callbacks);
    for (Map.Entry<Integer, ArrayList<InputCallback>> entry : keyBindings.entrySet()) {
      entry.getValue().removeAll(callbackHashSet);
    }
  }

  public EventBus events() {
    return eventBus;
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
