package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class AlertDialog extends Window {
  private Skin skin;
  private Label messageLabel;
  private final Table buttonContainer;

  public AlertDialog(Skin skin) {
    super(skin);
    this.skin = skin;

    defaults();

    setModal(true);
    setMovable(true);

    setTitle("Test Dialog");
    row();
    setMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed dictum posuere mattis. Mauris condimentum nisl diam. Duis et nibh at arcu pretium porta vestibulum et.  Duis et nibh at arcu pretium porta vestibulum et.");
    row();
    buttonContainer = new Table("buttonContainer");
    buttonContainer.defaults();
    add(buttonContainer);

    addButton("Yes");
    addButton("No");

    messageLabel.pack();
    buttonContainer.pack();

    pack();
  }

  private void addButton(String labelText) {
    LabelButton button = new LabelButton(skin, labelText);
    buttonContainer.add(button).fill().minWidth(80);
  }

  private void setMessage(String message) {
    if (messageLabel == null) {
      messageLabel = new Label(skin);
      messageLabel.setWrap(true);
      add(messageLabel).minWidth(250).maxWidth(600).fill();
    }
    messageLabel.setText(message);

  }
}
