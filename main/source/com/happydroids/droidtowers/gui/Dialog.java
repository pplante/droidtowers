/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.Scene;

import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class Dialog {
  static final int[] NEGATIVE_BUTTON_KEYS = new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE};
  private Stage stage;
  private Skin skin;
  private List<TextButton> buttons;
  private TextButton positiveButton;
  private TextButton negativeButton;
  private String title;
  private String messageText;
  private TowerWindowTwo window;
  private boolean shouldDisplayCentered;
  private final InputCallback positiveButtonInputCallback;
  private final InputCallback negativeButtonInputCallback;

  public Dialog() {
    this(TowerGame.getActiveScene().getStage());
  }

  public Dialog(Stage stage) {
    this.stage = stage;
    skin = Scene.getGuiSkin();
    title = "Dialog";
    buttons = Lists.newArrayList();

    positiveButtonInputCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        positiveButton.click(1, 1);
        return true;
      }
    };

    negativeButtonInputCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        dismiss();

        return true;
      }
    };
  }

  public Dialog setTitle(String title) {
    this.title = title;

    return this;
  }

  public Dialog setMessage(String message) {
    messageText = message;

    return this;
  }

  public Dialog addButton(ResponseType type, String labelText, final OnClickCallback onClickCallback) {
    TextButton button = FontManager.RobotoBold18.makeTextButton(labelText, skin);

    if (type == ResponseType.NEGATIVE) {
      negativeButton = button;
    } else if (type == ResponseType.POSITIVE) {
      positiveButton = button;
    }

    if (onClickCallback != null) {
      button.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          onClickCallback.onClick(Dialog.this);
        }
      });
    }

    buttons.add(button);

    return this;
  }

  public Dialog addButton(String buttonLabel, OnClickCallback onClickCallback) {
    return addButton(ResponseType.NEUTRAL, buttonLabel, onClickCallback);
  }

  public Dialog addButton(String buttonText) {
    return addButton(buttonText, new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    });
  }

  public Dialog show() {
    ModalOverlay.instance().show(stage);

    window = new TowerWindowTwo(title, stage, skin);

    Table container = new Table(skin);
    container.defaults().pad(scale(12)).top().left();
    Label messageLabel = FontManager.Roboto24.makeLabel(messageText);
    messageLabel.setWrap(true);

    container.row();
    container.add(messageLabel).colspan(buttons.size()).width(Display.percentOfScreen(0.8f)).minHeight(scale(120));

    container.row();
    for (TextButton button : buttons) {
      container.add(button).fill();
    }
    container.pack();

    window.add(container);
    window.show();

    if (positiveButton != null) {
      InputSystem.instance().bind(InputSystem.Keys.ENTER, positiveButtonInputCallback);
    }

    if (negativeButton != null) {
      InputSystem.instance().bind(NEGATIVE_BUTTON_KEYS, negativeButtonInputCallback);
    }

    return this;
  }

  public void dismiss() {
    if (window != null) {
      window.dismiss();
      window = null;
      InputSystem.instance().unbind(NEGATIVE_BUTTON_KEYS, negativeButtonInputCallback);
      ModalOverlay.instance().hide();
    }
  }
}
