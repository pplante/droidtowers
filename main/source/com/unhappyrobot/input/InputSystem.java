package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.unhappyrobot.entities.GameLayer;

import java.util.*;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  private List<InputProcessorEntry> inputProcessors;
  private List<InputProcessorEntry> inputProcessorsSorted;

  private HashMap<Integer, ArrayList<InputCallback>> keyBindings;

  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;
  private GestureDelegater delegater;

  private static InputSystem instance;
  private List<GameLayer> gameLayers;

  public static InputSystem instance() {
    if (instance == null) {
      instance = new InputSystem();
    }

    return instance;
  }

  public void setup(OrthographicCamera orthographicCamera, List<GameLayer> gameLayers) {
    this.gameLayers = gameLayers;
    inputProcessors = Lists.newArrayList();
    keyBindings = Maps.newHashMap();
    camera = orthographicCamera;
    if (gameLayers != null) {
      delegater = new GestureDelegater(camera, gameLayers);
      gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, delegater);
      switchTool(GestureTool.PICKER, null);

      addInputProcessor(gestureDetector, 100);
    }
  }

  public void addInputProcessor(InputProcessor inputProcessor, int priority) {
    inputProcessors.add(new InputProcessorEntry(inputProcessor, priority));

    sortInputProcessors();
  }

  private void sortInputProcessors() {
    inputProcessorsSorted = Ordering.from(new Comparator<InputProcessorEntry>() {
      public int compare(InputProcessorEntry entryA, InputProcessorEntry entryB) {
        return entryA.getPriority() - entryB.getPriority();
      }
    }).sortedCopy(inputProcessors);
  }

  public void removeInputProcessor(InputProcessor inputProcessor) {
    Iterator<InputProcessorEntry> iterator = inputProcessors.iterator();

    while (iterator.hasNext()) {
      InputProcessorEntry entry = iterator.next();
      if (entry.getInputProcessor().equals(inputProcessor)) {
        iterator.remove();
      }
    }

    sortInputProcessors();
  }

  public void switchTool(GestureTool selectedTool, Runnable switchToolRunnable) {
    if(delegater != null) {
      delegater.switchTool(camera, gameLayers, selectedTool, switchToolRunnable);
    }
  }

  public GestureListener getCurrentTool() {
    return delegater != null ? delegater.getCurrentTool() : null;
  }

  public void bind(int keyCode, InputCallback inputCallback) {
    ArrayList<InputCallback> inputCallbacks = getBindingsForKeyCode(keyCode);
    inputCallback.addBoundKey(keyCode);
    inputCallbacks.add(0, inputCallback);
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
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().keyDown(keycode)) {
        return true;
      }
    }

    if (keyBindings.containsKey(keycode)) {
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

  public boolean touchDown(int x, int y, int pointer, int button) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().touchDown(x, y, pointer, button)) {
        return true;
      }
    }

    return false;
  }

  public boolean touchUp(int x, int y, int pointer, int button) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().touchUp(x, y, pointer, button)) {
        return true;
      }
    }

    return false;
  }

  public boolean touchDragged(int x, int y, int pointer) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().touchDragged(x, y, pointer)) {
        return true;
      }
    }

    return false;
  }

  public boolean touchMoved(int x, int y) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().touchMoved(x, y)) {
        return true;
      }
    }

    return false;
  }

  public boolean scrolled(int amount) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().scrolled(amount)) {
        return true;
      }
    }

    return delegater != null && delegater.scrolled(amount);
  }

  public void update(float deltaTime) {
    if (delegater != null) {
      delegater.update(deltaTime);
    }
  }

  public void unbind(int keyCode, InputCallback inputCallbackToRemove) {
    if (!keyBindings.containsKey(keyCode)) {
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

  public static class Keys extends Input.Keys {
  }
}
