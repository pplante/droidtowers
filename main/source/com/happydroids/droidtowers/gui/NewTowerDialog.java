/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class NewTowerDialog extends Dialog {

  public static final int ROW_SPACE = scale(16);

  private DifficultyLevel difficultyLevel;

  public NewTowerDialog(Stage stage) {
    super(stage);

    setTitle("Start a new Tower");

    Table content = new Table();
    content.defaults().top().left().space(ROW_SPACE);
    content.row();
    content.add(FontManager.RobotoBold18.makeLabel("Tower Name:")).right();

    final TextField nameField = FontManager.Roboto24.makeTextField("", "");
    content.add(nameField).fillX().expandX().left();
    content.row().space(ROW_SPACE);

    content.add(FontManager.RobotoBold18.makeLabel("Difficulty:")).right();

    TextButton easy = FontManager.RobotoBold18.makeTextButton("Easy");
    TextButton medium = FontManager.RobotoBold18.makeTextButton("Medium");
    TextButton hard = FontManager.RobotoBold18.makeTextButton("Hard");

    Table buttonContainer = new Table();
    buttonContainer.row().pad(4).fill();
    buttonContainer.add(easy).expand();
    buttonContainer.add(medium).expand();
    buttonContainer.add(hard).expand();

    content.add(buttonContainer).fillX();
    content.row();

    final String moneyLabelPrefix = "Starting money: " + TowerConsts.CURRENCY_SYMBOL;
    final Label moneyLabel = FontManager.Roboto32.makeLabel(moneyLabelPrefix);
    content.add(moneyLabel).center().colspan(2);

    final ButtonGroup difficultyGroup = new ButtonGroup(easy, medium, hard);
    difficultyGroup.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Button checked = difficultyGroup.getChecked();
        if (checked != null) {
          String buttonText = ((TextButton) checked).getText().toString();
          difficultyLevel = DifficultyLevel.valueOf(buttonText.toUpperCase());
          moneyLabel.setText(moneyLabelPrefix + NumberFormat.getInstance().format(difficultyLevel.getStartingMoney()));
          NewTowerDialog.this.pack();
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

    addButton("cancel", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dismiss();
      }
    });

    addButton("Build!", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        if (StringUtils.isEmpty(nameField.getText())) {
          new Dialog()
                  .setTitle("Error")
                  .setMessage("You need to provide a name for your Tower!")
                  .show();
          return;
        }
        dismiss();
        SceneManager.changeScene(TowerScene.class, new GameSave(nameField.getText(), difficultyLevel));
      }
    });

    setView(content);
  }
}
