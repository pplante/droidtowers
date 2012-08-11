/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.LoadTowerSplashScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.StringUtils;

public class NewTowerDialog extends Dialog {
  public static final String MONEY_LABEL_PREFIX = "Starting money: ";
  public static final int ROW_SPACE = Display.devicePixel(16);

  private DifficultyLevel difficultyLevel;
  private final Label moneyLabel;

  public NewTowerDialog(Stage stage) {
    super(stage);

    setTitle("Start a new Tower");

    final TextField nameField = FontManager.Roboto24.makeTextField("", "");

    TextButton randomNameButton = FontManager.Roboto12.makeTextButton("Random Name");
    randomNameButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
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

    TextButton easyButton = FontManager.RobotoBold18.makeTextToggleButton("Easy");
    TextButton mediumButton = FontManager.RobotoBold18.makeTextToggleButton("Medium");
    TextButton hardButton = FontManager.RobotoBold18.makeTextToggleButton("Hard");

    Table buttonContainer = new Table();
    buttonContainer.row().pad(4).fill();
    buttonContainer.add(easyButton).expand();
    buttonContainer.add(mediumButton).expand();
    buttonContainer.add(hardButton).expand();

    c.add(buttonContainer).fillX().colspan(2);
    c.row();

    moneyLabel = FontManager.Roboto32.makeLabel(MONEY_LABEL_PREFIX);
    c.add(moneyLabel).center().colspan(3);

    easyButton.setChecked(true);
    easyButton.addListener(new DifficultyLevelButtonListener(DifficultyLevel.EASY, moneyLabel));
    mediumButton.addListener(new DifficultyLevelButtonListener(DifficultyLevel.MEDIUM, moneyLabel));
    hardButton.addListener(new DifficultyLevelButtonListener(DifficultyLevel.HARD, moneyLabel));

    final ButtonGroup difficultyGroup = new ButtonGroup(easyButton, mediumButton, hardButton);
    difficultyGroup.setUncheckLast(true);
    difficultyGroup.setMaxCheckCount(1);

    TextButton cancelButton = FontManager.RobotoBold18.makeTextButton("cancel");
    cancelButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
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
        } else {
          dismiss();
          SceneManager.changeScene(LoadTowerSplashScene.class, new GameSave(nameField.getText(), difficultyLevel));
        }
      }
    });

    setView(c);

    setDifficultyLevel(DifficultyLevel.EASY);
  }

  private class DifficultyLevelButtonListener extends InputListener {
    private final DifficultyLevel buttonDifficultyLevel;

    public DifficultyLevelButtonListener(DifficultyLevel buttonDifficultyLevel, Label moneyLabel) {
      this.buttonDifficultyLevel = buttonDifficultyLevel;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
      setDifficultyLevel(buttonDifficultyLevel);
      return true;
    }
  }

  public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
    this.difficultyLevel = difficultyLevel;
    moneyLabel.setText(MONEY_LABEL_PREFIX + StringUtils.currencyFormat(difficultyLevel.getStartingMoney()));
  }
}
