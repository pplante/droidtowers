package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class AlertDialog extends Window {
  private Skin skin;
  private Label messageLabel;

  public AlertDialog(Skin skin) {
    super(skin);
    this.skin = skin;

    defaults();
//    debug("all");
    setModal(true);
    setMovable(true);

    setTitle("Test Dialog");
    setMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed dictum posuere mattis. Mauris condimentum nisl diam. Duis et nibh at arcu pretium porta vestibulum et.  Duis et nibh at arcu pretium porta vestibulum et.");
    row();

    addButton("Yes");
    addButton("No");

    pack();
  }

  private void addButton(String labelText) {
    LabelButton button = new LabelButton(skin, labelText);
    add(button).fill().minWidth(80);
  }

  private void setMessage(String message) {
    if (messageLabel == null) {
      messageLabel = new Label(skin);
      messageLabel.setWrap(true);
      messageLabel.width = 400;
      messageLabel.setText(message);
      messageLabel.layout();
      BitmapFont.TextBounds textBounds = messageLabel.getTextBounds();
      add(messageLabel).width(Gdx.graphics.getWidth() / 2).minWidth(400).maxWidth(600).fill().colspan(2);
    } else {
      messageLabel.setText(message);
    }
  }
}
