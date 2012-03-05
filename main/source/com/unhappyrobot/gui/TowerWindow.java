package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;

public class TowerWindow extends Window {
  private static final int[] DIALOG_CLOSE_KEYCODES = new int[]{InputSystem.Keys.ESCAPE, InputSystem.Keys.BACK};
  private InputCallback closeDialogCallback;
  private Runnable dismissCallback;
  private boolean modalState;
  protected final Skin skin;

  public TowerWindow(String title) {
    super(title, HeadsUpDisplay.instance().getGuiSkin());
    skin = HeadsUpDisplay.instance().getGuiSkin();

    defaults().top().left();
  }

  public TowerWindow show() {
    closeDialogCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        TowerWindow.this.dismiss();
        return true;
      }
    };

    InputSystem.instance().bind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);

    pack();
    HeadsUpDisplay.instance().getStage().addActor(this);

    return this;
  }

  public void dismiss() {
    if (closeDialogCallback != null) {
      InputSystem.instance().unbind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);
      closeDialogCallback = null;
    }

    if (dismissCallback != null) {
      dismissCallback.run();
    }

    if (modalState) {
      ModalOverlay.instance().hide();
    }

    stage.setScrollFocus(null);
    stage.setKeyboardFocus(null);

    markToRemove(true);
  }

  public void setDismissCallback(Runnable dismissCallback) {
    this.dismissCallback = dismissCallback;
  }

  public TowerWindow centerOnStage() {
    x = (stage.width() - width) / 2;
    y = (stage.height() - height) / 2;

    return this;
  }

  public TowerWindow modal(boolean newState) {
    this.modalState = newState;

    if (modalState) {
      setModal(true);
      setMovable(false);
      ModalOverlay.instance().show();
    } else {
      setModal(false);
      setMovable(true);
      ModalOverlay.instance().hide();
    }

    return this;
  }
}
