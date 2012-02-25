package com.unhappyrobot.gui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.tween.TweenSystem;

import java.util.List;

public class Dialog {
  public static final int[] NEGATIVE_BUTTON_KEYS = new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE};
  private Stage parent;
  private Skin skin;
  private List<LabelButton> buttons;
  private String title;
  private String messageText;
  private Window window;
  private boolean shouldDisplayCentered;
  private LabelButton positiveButton;
  private LabelButton negativeButton;
  private final InputCallback positiveButtonInputCallback;
  private final InputCallback negativeButtonInputCallback;
  private InputCallback onDismissInputCallback;

  public Dialog() {
    parent = HeadsUpDisplay.instance().getStage();
    skin = HeadsUpDisplay.instance().getGuiSkin();
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
    LabelButton button = new LabelButton(skin, labelText);
    button.setResponseType(type);

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
    if (window != null) {
      parent.removeActor(window);
      window = null;
    }

    ModalOverlay.instance().show();

    window = new Window(title, skin);

    window.defaults();
    window.setMovable(false);
    window.setModal(true);

    Table table = new Table(skin);
    table.defaults();
    table.row().pad(10).space(4);
    Label messageLabel = new Label(skin);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.TOP | Align.LEFT);
    messageLabel.setText(messageText);

    table.add(messageLabel).top().left().width((int) (parent.width() / 2)).minWidth(400).maxWidth(600).fill().colspan(buttons.size());

    table.row().space(4);
    for (LabelButton button : buttons) {
      table.add(button).fill().minWidth(80);
    }

    table.pack();

    window.add(table);
    window.pack();

    parent.addActor(window);

    if (shouldDisplayCentered) {
      centerOnScreen();
    }

    if (positiveButton != null) {
      InputSystem.instance().bind(InputSystem.Keys.ENTER, positiveButtonInputCallback);
    }

    if (negativeButton != null) {
      InputSystem.instance().bind(NEGATIVE_BUTTON_KEYS, negativeButtonInputCallback);
    }

    window.color.a = 0f;

    Timeline.createSequence()
            .push(Tween.set(window, WidgetAccessor.OPACITY).target(0f))
            .push(Tween.to(window, WidgetAccessor.OPACITY, 200).delay(100).target(1.0f))
            .start(TweenSystem.getTweenManager());

    return this;
  }

  public void dismiss() {
    if (parent != null && window != null) {
      parent.removeActor(window);
    }

    InputSystem.instance().unbind(InputSystem.Keys.ENTER, positiveButtonInputCallback);
    InputSystem.instance().unbind(NEGATIVE_BUTTON_KEYS, negativeButtonInputCallback);

    if (onDismissInputCallback != null) {
      onDismissInputCallback.run(0f);
    }

    ModalOverlay.instance().hide();
  }

  public Dialog centerOnScreen() {
    if (window != null) {

      window.x = (parent.width() - window.width) / 2;
      window.y = (parent.height() - window.height) / 2;
    } else {
      shouldDisplayCentered = true;
    }

    return this;
  }

  public void onDismiss(InputCallback inputCallback) {
    onDismissInputCallback = inputCallback;
  }
}
