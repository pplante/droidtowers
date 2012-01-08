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
import com.unhappyrobot.entities.GameGrid;

import java.util.*;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputSystem extends InputAdapter {
  private List<InputProcessorEntry> inputProcessors;
  private List<InputProcessorEntry> inputProcessorsSorted;

  private HashMap<Integer, ArrayList<Action>> keyBindings;

  private OrthographicCamera camera;
  private CameraController cameraController;
  private GestureDetector gestureDetector;
  private GestureDelegater delegater;

  private static InputSystem instance;
  private GameGrid gameGrid;

  public static InputSystem getInstance() {
    if (instance == null) {
      instance = new InputSystem();
    }

    return instance;
  }

  public void setup(OrthographicCamera orthographicCamera, GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    inputProcessors = Lists.newArrayList();
    keyBindings = Maps.newHashMap();
    camera = orthographicCamera;
    delegater = new GestureDelegater(camera, gameGrid);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, delegater);
    switchTool(GestureTool.PICKER, null);

    addInputProcessor(gestureDetector, 100);
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
    delegater.switchTool(camera, gameGrid, selectedTool, switchToolRunnable);
  }

  public GestureListener getCurrentTool() {
    return delegater.getCurrentTool();
  }

  public void bind(int keyCode, Action action) {
    ArrayList<Action> actions = getBindingsForKeyCode(keyCode);

    actions.add(0, action);
  }

  public void bind(int[] keyCodes, Action action) {
    for (int keyCode : keyCodes) {
      bind(keyCode, action);
    }
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

  @SuppressWarnings("WhileLoopReplaceableByForEach")
  public boolean keyDown(int keycode) {
    for (InputProcessorEntry entry : inputProcessorsSorted) {
      if (entry.getInputProcessor().keyDown(keycode)) {
        return true;
      }
    }

    if (keyBindings.containsKey(keycode)) {
      float deltaTime = Gdx.graphics.getDeltaTime();

      List<Action> actionsForKeyCode = Lists.newArrayList(keyBindings.get(keycode));
      for (Action action : actionsForKeyCode) {
        if (action.run(deltaTime)) {
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

    return delegater.getCameraController().scrolled(amount);
  }

  public void update(float deltaTime) {
    delegater.update(deltaTime);
  }

  public void unbind(int keyCode, Action actionToRemove) {
    if (!keyBindings.containsKey(keyCode)) {
      return;
    }

    Iterator<Action> iterator = keyBindings.get(keyCode).iterator();
    while (iterator.hasNext()) {
      Action action = iterator.next();
      if (action == actionToRemove) {
        iterator.remove();
      }
    }
  }

  public void unbind(int[] keyCodes, Action action) {
    for (int keyCode : keyCodes) {
      unbind(keyCode, action);
    }
  }

  public static class Keys extends Input.Keys {
  }
}
