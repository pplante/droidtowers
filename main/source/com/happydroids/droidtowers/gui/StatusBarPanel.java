/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class StatusBarPanel extends Table {
  private final Skin guiSkin;
  private final TowerScene towerScene;
  private final Label moneyLabel;
  private final Label experienceLabel;
  private final Label gameSpeedLabel;
  private final Label populationLabel;
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;
  private final Label moneyIncomeLabel;
  private final Label moneyExpensesLabel;

  public StatusBarPanel(Skin guiSkin, TowerScene towerScene) {
    this.guiSkin = guiSkin;
    this.towerScene = towerScene;

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel("0x");

    setBackground(guiSkin.getPatch("default-round"));

    defaults();
    top().left().padTop(scale(8)).padLeft(scale(8));

    row().center();
    makeHeader("EXPERIENCE");
    makeHeader("COINS");
    makeHeader("INCOME");
    makeHeader("EXPENSES");
    makeHeader("POPULATION");
    makeHeader("GAME SPEED");

    row().center().padRight(12);
    add(experienceLabel);
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(gameSpeedLabel);

    pack();
  }

  private Label makeValueLabel(String labelText) {
    Label label = FontManager.Default.makeLabel(labelText);
    label.setAlignment(Align.CENTER);
    return label;
  }

  private Cell makeHeader(String headerText) {
    Label label = FontManager.Roboto12.makeLabel(headerText);
    label.setAlignment(Align.CENTER);
    label.setColor(Color.LIGHT_GRAY);

    return add(label).center().padRight(12);
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    lastUpdated += delta;

    if (lastUpdated >= TowerConsts.HUD_UPDATE_FREQUENCY) {
      lastUpdated = 0f;
      Player player = Player.instance();
      experienceLabel.setText(NumberFormat.getInstance().format(player.getExperience()));
      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + NumberFormat.getInstance().format(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + NumberFormat.getInstance().format(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + NumberFormat.getInstance().format(player.getCurrentExpenses()));
      populationLabel.setText(NumberFormat.getInstance().format(player.getTotalPopulation()));

      gameSpeedLabel.setText(towerScene.getTimeMultiplier() + "x");

      pack();
    }
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    return true;
  }
}
