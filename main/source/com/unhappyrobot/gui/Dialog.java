package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;

import java.util.List;

public class Dialog {
  private Group parent;
  private Skin skin;
  private List<LabelButton> buttons;
  private String title;
  private String messageText;
  private Window window;
  private boolean shouldDisplayCentered;

  public Dialog() {
    this.parent = HeadsUpDisplay.getInstance();
    this.skin = HeadsUpDisplay.getInstance().getSkin();

    buttons = Lists.newArrayList();
  }

  public Dialog setTitle(String title) {
    this.title = title;

    return this;
  }

  public Dialog setMessage(String message) {
    messageText = message;

    return this;
  }

  public Dialog addButton(String labelText, final OnClickCallback onClickCallback) {
    LabelButton button = new LabelButton(skin, labelText);
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

  public Dialog show() {
    if (window != null) {
      parent.removeActor(window);
      window = null;
    }

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

    table.add(messageLabel).top().left().width(Gdx.graphics.getWidth() / 2).minWidth(400).maxWidth(600).fill().colspan(buttons.size());

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

    return this;
  }

  public void dismiss() {
    if (parent != null && window != null) {
      parent.removeActor(window);
    }
  }

  public Dialog centerOnScreen() {
    if (window != null) {
      float parentWidth = parent.width > 0 ? parent.width : Gdx.graphics.getWidth();
      float parentHeight = parent.height > 0 ? parent.height : Gdx.graphics.getHeight();

      window.x = (parentWidth - window.width) / 2;
      window.y = (parentHeight - window.height) / 2;
    } else {
      shouldDisplayCentered = true;
    }

    return this;
  }

}
