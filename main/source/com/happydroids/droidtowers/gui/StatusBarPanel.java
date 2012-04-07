/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;

import java.text.NumberFormat;

public class StatusBarPanel extends Table {
  private final Skin guiSkin;
  private final TowerScene towerScene;
  private final Label moneyLabel;
  private final Label experienceLabel;
  private final Label gameSpeedLabel;
  private final Label populationLabel;
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;

  public StatusBarPanel(Skin guiSkin, TowerScene towerScene) {
    this.guiSkin = guiSkin;
    this.towerScene = towerScene;
    Label.LabelStyle helvetica_neue_10_bold_white = this.guiSkin.getStyle("helvetica_neue_10_bold_white", Label.LabelStyle.class);

    moneyLabel = makeLabel("0", null);
    experienceLabel = makeLabel("0", null);
    populationLabel = makeLabel("0", null);
    gameSpeedLabel = makeLabel("0x", null);

    setBackground(guiSkin.getPatch("default-round"));

    defaults();
    top().left().padTop(8).padLeft(8);

    row().center();
    add(makeLabel("EXPERIENCE", helvetica_neue_10_bold_white)).minWidth(100);
    add(makeLabel("COINS", helvetica_neue_10_bold_white)).minWidth(100);
    add(makeLabel("POPULATION", helvetica_neue_10_bold_white)).minWidth(100);
    add(makeLabel("GAME SPEED", helvetica_neue_10_bold_white)).minWidth(100);

    row().center();
    add(experienceLabel);
    add(moneyLabel);
    add(populationLabel);
    add(gameSpeedLabel);

    pack();
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
      populationLabel.setText(NumberFormat.getInstance().format(player.getTotalPopulation()));

      gameSpeedLabel.setText(towerScene.getTimeMultiplier() + "x");

      pack();
    }
  }

  private Label makeLabel(String text, Label.LabelStyle labelStyle) {
    Label label = new Label(text, guiSkin);
    label.setAlignment(Align.CENTER);

    if (labelStyle != null) {
      label.setStyle(labelStyle);
    }

    return label;
  }
}
