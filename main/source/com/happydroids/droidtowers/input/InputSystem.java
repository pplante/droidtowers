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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GameLayer;

import java.util.*;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  private static final String TAG = InputSystem.class.getSimpleName();

  private List<InputProcessorEntry> inputProcessors;
  private List<InputProcessorEntry> inputProcessorsSorted;

  private HashMap<Integer, ArrayList<InputCallback>> keyBindings;
  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;

  private GestureDelegater gestureDelegater;
  private static InputSystem instance;
  private List<GameLayer> gameLayers;

  public static InputSystem instance() {
    if (instance == null) {
      instance = new InputSystem();
    }

    return instance;
  }

  public InputSystem() {
    inputProcessors = Lists.newArrayList();
    keyBindings = Maps.newHashMap();
  }

  public void addInputProcessor(InputProcessor inputProcessor, int priority) {
    if (inputProcessors != null) {
      inputProcessors.add(new InputProcessorEntry(inputProcessor, priority));

      sortInputProcessors();
    }
  }

  private void sortInputProcessors() {
    inputProcessorsSorted = Ordering.from(new Comparator<InputProcessorEntry>() {
      public int compare(InputProcessorEntry entryA, InputProcessorEntry entryB) {
        return entryA.getPriority() - entryB.getPriority();
      }
    }).sortedCopy(inputProcessors);
  }

  public void removeInputProcessor(InputProcessor inputProcessor) {
    if (inputProcessors != null) {
      Iterator<InputProcessorEntry> iterator = inputProcessors.iterator();

      while (iterator.hasNext()) {
        InputProcessorEntry entry = iterator.next();
        if (entry.getInputProcessor().equals(inputProcessor)) {
          iterator.remove();
        }
      }

      sortInputProcessors();
    }
  }

  public void switchTool(GestureTool selectedTool, Runnable switchToolRunnable) {
    if (gestureDelegater != null) {
      gestureDelegater.switchTool(TowerGame.getActiveScene().getCamera(), gestureDelegater.getGameLayers(), selectedTool, switchToolRunnable);
    }
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
    for (int keyCode : keyCodes) {
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
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().keyDown(keycode)) {
          return true;
        }
      }
    }

    if (keyBindings != null && keyBindings.containsKey(keycode)) {
      float deltaTime = Gdx.graphics.getDeltaTime();

      List<InputCallback> actionsForKeyCode = Lists.newArrayList(keyBindings.get(keycode));
      for (InputCallback inputCallback : actionsForKeyCode) {
        if (inputCallback.run(deltaTime)) {
          break;
        }
      }
      return true;
    }

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().keyTyped(character)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().keyUp(keycode)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchDown(int x, int y, int pointer, int button) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().touchDown(x, y, pointer, button)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchUp(int x, int y, int pointer, int button) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().touchUp(x, y, pointer, button)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchDragged(int x, int y, int pointer) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().touchDragged(x, y, pointer)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean touchMoved(int x, int y) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().touchMoved(x, y)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean scrolled(int amount) {
    if (inputProcessorsSorted != null) {
      for (InputProcessorEntry entry : inputProcessorsSorted) {
        if (entry.getInputProcessor().scrolled(amount)) {
          return true;
        }
      }
    }

    return gestureDelegater != null && gestureDelegater.scrolled(amount);
  }

  public void update(float deltaTime) {
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
    for (int keyCode : keyCodes) {
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

    for (InputProcessorEntry entry : inputProcessors) {
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

  public static class Keys extends Input.Keys {
  }
}
