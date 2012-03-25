package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.DifficultyLevel;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.TowerMetadata;
import com.unhappyrobot.scenes.TowerScene;

import java.text.NumberFormat;

public class NewGameWindow extends TowerWindow {

  private DifficultyLevel difficultyLevel;
  private final TextButton beginButton;

  public NewGameWindow(Stage stage, Skin skin) {
    super("Start a new Tower", stage, skin);

    defaults().top().left().pad(5);
    add(LabelStyles.Default.makeLabel("Please provide a name for your Tower:"));
    row().colspan(2);

    final TextField nameField = new TextField("", "Tower Name", skin);
    nameField.setTextFieldListener(new TextField.TextFieldListener() {
      public void keyTyped(TextField textField, char key) {
        beginButton.touchable = !textField.getText().isEmpty();

        if (beginButton.touchable) {
          beginButton.action(FadeIn.$(0.125f));
        } else {
          beginButton.action(FadeTo.$(0.6f, 0.125f));
        }
      }
    });
    add(nameField);
    row().padTop(15).colspan(2);

    add(LabelStyles.Default.makeLabel("Select level of difficulty:"));
    row().colspan(2);

    TextButton easy = new CheckBox(" Easy", skin);
    TextButton medium = new CheckBox(" Medium", skin);
    TextButton hard = new CheckBox(" Hard", skin);

    Table buttonContainer = new Table(skin);
    buttonContainer.row().pad(4);
    buttonContainer.add(easy).expand();
    buttonContainer.add(medium).expand();
    buttonContainer.add(hard).expand();

    add(buttonContainer).center().fill();
    row().padTop(15).colspan(2);

    final String moneyLabelPrefix = "Starting money: ";
    final Label moneyLabel = LabelStyles.Default.makeLabel(moneyLabelPrefix);
    add(moneyLabel);

    final ButtonGroup difficultyGroup = new ButtonGroup(easy, medium, hard);
    difficultyGroup.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Button checked = difficultyGroup.getChecked();
        if (checked != null) {
          String buttonText = ((TextButton) checked).getText().toString();
          difficultyLevel = DifficultyLevel.valueOf(buttonText.substring(1).toUpperCase());
          moneyLabel.setText(moneyLabelPrefix + NumberFormat.getCurrencyInstance().format(difficultyLevel.getStartingMoney()));
        }
      }
    });

    difficultyGroup.setChecked(" Easy");

    row().padTop(25);
    TextButton cancel = new TextButton("Cancel", skin);
    cancel.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    add(cancel).right();
    beginButton = new TextButton("Begin building!", skin);
    beginButton.touchable = false;
    beginButton.action(FadeTo.$(0.6f, 0f));
    add(beginButton).right();

    beginButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
        TowerMetadata towerMetadata = new TowerMetadata();
        towerMetadata.setName(nameField.getText());
        towerMetadata.setDifficulty(difficultyLevel);

        TowerGame.changeScene(TowerScene.class, towerMetadata);
      }
    });
  }
}
