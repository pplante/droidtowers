/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.TowerScene;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class NewGameWindow extends TowerWindow {

  public static final int ROW_SPACE = scale(16);

  private DifficultyLevel difficultyLevel;
  private final TextButton beginButton;

  public NewGameWindow(Stage stage, Skin skin) {
    super("Start a new Tower", stage);

    clear();

    row().space(ROW_SPACE).padTop(scale(32));
    add(FontManager.RobotoBold18.makeLabel("Please provide a name for your Tower:")).right();

    final TextField nameField = FontManager.Roboto32.makeTextField("", "Tower Name");
    nameField.setTextFieldListener(new TextField.TextFieldListener() {
      public void keyTyped(TextField textField, char key) {
        beginButton.touchable = textField.getText().length() > 1;

        if (beginButton.touchable) {
          beginButton.action(FadeIn.$(0.125f));
        } else {
          beginButton.action(FadeTo.$(0.6f, 0.125f));
        }
      }
    });
    add(nameField).fillX().expandX().left();
    row().space(ROW_SPACE);

    add(FontManager.RobotoBold18.makeLabel("Select level of difficulty:")).right();

    TextButton easy = FontManager.RobotoBold18.makeCheckBox("Easy");
    TextButton medium = FontManager.RobotoBold18.makeCheckBox("Medium");
    TextButton hard = FontManager.RobotoBold18.makeCheckBox("Hard");

    Table buttonContainer = new Table(skin);
    buttonContainer.row().pad(4);
    buttonContainer.add(easy).expand();
    buttonContainer.add(medium).expand();
    buttonContainer.add(hard).expand();

    add(buttonContainer).fill();
    row().space(ROW_SPACE).colspan(2);

    final String moneyLabelPrefix = "Starting money: ";
    final Label moneyLabel = FontManager.Roboto32.makeLabel(moneyLabelPrefix);
    add(moneyLabel);

    final ButtonGroup difficultyGroup = new ButtonGroup(easy, medium, hard);
    difficultyGroup.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Button checked = difficultyGroup.getChecked();
        if (checked != null) {
          String buttonText = ((TextButton) checked).getText().toString();
          difficultyLevel = DifficultyLevel.valueOf(buttonText.toUpperCase());
          moneyLabel.setText(moneyLabelPrefix + NumberFormat.getCurrencyInstance().format(difficultyLevel.getStartingMoney()));
        }
      }
    });

    difficultyGroup.setChecked("Easy");

    TextButton cancelButton = FontManager.RobotoBold18.makeTextButton("cancel");
    cancelButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    beginButton = FontManager.RobotoBold18.makeTextButton("Begin Building!");
    beginButton.touchable = false;
    beginButton.action(FadeTo.$(0.6f, 0f));

    beginButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
        TowerGame.changeScene(TowerScene.class, new GameSave(nameField.getText(), difficultyLevel));
      }
    });

    Table bottomBar = new Table();
    bottomBar.row().space(ROW_SPACE);
    bottomBar.add(cancelButton);
    bottomBar.add(beginButton);


    row().colspan(2).expandY().bottom().right().padBottom(ROW_SPACE);
    add(bottomBar);
  }
}
