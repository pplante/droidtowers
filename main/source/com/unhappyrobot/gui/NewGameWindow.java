package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.scenes.TowerScene;

import java.text.NumberFormat;

public class NewGameWindow extends TowerWindow {
  public NewGameWindow(String title, Stage stage, Skin skin) {
    super(title, stage, skin);

    defaults().top().left().pad(5);
    add(LabelStyles.Default.makeLabel("Please provide a name for your Tower:"));
    row().colspan(2);

    TextField nameField = new TextField("", "Tower Name", skin);
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
          int amountOfMoney = 50000;
          if (buttonText.contains("Medium")) {
            amountOfMoney = 35000;
          } else if (buttonText.contains("Hard")) {
            amountOfMoney = 10000;
          }

          moneyLabel.setText(moneyLabelPrefix + NumberFormat.getCurrencyInstance().format(amountOfMoney));
        }
      }
    });

    difficultyGroup.setChecked(" Easy");

    row().padTop(25);
    add(new TextButton("Cancel", skin)).right();
    TextButton beginButton = new TextButton("Begin building!", skin);
    add(beginButton).right();

    beginButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.changeScene(TowerScene.class);
      }
    });
  }
}
