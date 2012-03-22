package com.unhappyrobot;

import com.badlogic.gdx.*;
import com.unhappyrobot.actions.ActionManager;
import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.input.InputSystem;
import org.lwjgl.opengl.Display;

public class LwjglApplicationShim implements ApplicationListener {
  private static final String TAG = LwjglApplicationShim.class.getSimpleName();
  private final ApplicationListener applicationListener;
  private boolean wasFocused;
  private InputProcessor inputProcessor;
  private final TimeDelayedAction reattachInputProcessorAction;
  private final InputProcessor dummyInputAdapter;

  public LwjglApplicationShim(ApplicationListener applicationListener) {
    this.applicationListener = applicationListener;
    reattachInputProcessorAction = new TimeDelayedAction(1f) {
      @Override
      public void run() {
        Gdx.app.debug(TAG, "Reattaching input processor.");
        Gdx.input.setInputProcessor(inputProcessor);
        inputProcessor = null;

        markToRemove();
      }
    };
    dummyInputAdapter = new InputAdapter();
  }

  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    wasFocused = isFocused();
    applicationListener.create();
  }

  public void resize(int width, int height) {
    applicationListener.resize(width, height);
  }

  public void render() {
    applicationListener.render();

    if (!isFocused() && wasFocused) {
      pause();
      wasFocused = false;
    }

    if (isFocused() && !wasFocused) {
      resume();
      wasFocused = true;
    }
  }

  public void pause() {
    Gdx.app.debug(TAG, "Focus lost, pausing game");
    if (Gdx.input.getInputProcessor() != dummyInputAdapter) {
      inputProcessor = Gdx.input.getInputProcessor();
    }

    if (inputProcessor instanceof InputSystem) {
      ((InputSystem) inputProcessor).unfocusAll();
    }

    Gdx.input.setInputProcessor(dummyInputAdapter);
    applicationListener.pause();
  }

  public void resume() {
    Gdx.app.debug(TAG, "Focus gained, resuming game");
    applicationListener.resume();

    reattachInputProcessorAction.reset();
    ActionManager.instance().addAction(reattachInputProcessorAction);
  }

  public void dispose() {
    applicationListener.dispose();
  }

  public boolean isFocused() {
    return Display.isActive();
  }
}
