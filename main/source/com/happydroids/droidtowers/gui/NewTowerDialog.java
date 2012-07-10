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
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.scenes.LoadTowerSplashScene;
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

    final TextField nameField = FontManager.Roboto24.makeTextField("", "");

    TextButton randomNameButton = FontManager.Roboto12.makeTextButton("Random Name");
    randomNameButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        nameField.setText(NameGenerator.randomCorporationName());
      }
    });

    Table c = new Table();
    c.defaults().top().left().space(ROW_SPACE);
    c.row();
    c.add(FontManager.RobotoBold18.makeLabel("Tower Name:")).right();

    c.add(nameField).fillX().expandX().left().minWidth(350);
    c.add(randomNameButton).fillY();

    c.row().space(ROW_SPACE);
    c.add(FontManager.RobotoBold18.makeLabel("Difficulty:")).right();

    TextButton easy = FontManager.RobotoBold18.makeTextButton("Easy");
    TextButton medium = FontManager.RobotoBold18.makeTextButton("Medium");
    TextButton hard = FontManager.RobotoBold18.makeTextButton("Hard");

    Table buttonContainer = new Table();
    buttonContainer.row().pad(4).fill();
    buttonContainer.add(easy).expand();
    buttonContainer.add(medium).expand();
    buttonContainer.add(hard).expand();

    c.add(buttonContainer).fillX().colspan(2);
    c.row();

    final String moneyLabelPrefix = "Starting money: " + TowerConsts.CURRENCY_SYMBOL;
    final Label moneyLabel = FontManager.Roboto32.makeLabel(moneyLabelPrefix);
    c.add(moneyLabel).center().colspan(3);

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
        SceneManager.changeScene(LoadTowerSplashScene.class, new GameSave(nameField.getText(), difficultyLevel));
      }
    });

    c.debug();

    setView(c);
  }
}
