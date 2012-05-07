/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class StatusBarPanel extends Table {
  private final TowerScene towerScene;
  private final Label moneyLabel;
  private final Label experienceLabel;
  private final Label gameSpeedLabel;
  private final Label populationLabel;
  private final Label employmentLabel;
  private final Label moneyIncomeLabel;
  private final Label moneyExpensesLabel;
  private final Label starRatingLabel;
  private final NoOpWidget starWidget;
  private final Texture starTexture;
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;
  private float starRating;

  public StatusBarPanel(TowerScene towerScene) {
    this.towerScene = towerScene;

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    employmentLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel("0x");
    starRatingLabel = makeValueLabel("0x");

    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults();
    center();
    pad(scale(4), scale(8), scale(4), scale(8));

    row().spaceRight(scale(8));
    makeHeader("COINS");
    makeHeader("INCOME");
    makeHeader("EXPENSES");
    makeHeader("POPULATION");
    makeHeader("EMPLOYMENT");
    makeHeader("GAME SPEED");

    Label label = FontManager.Roboto12.makeLabel("STAR RATING");
    label.setAlignment(Align.CENTER);
    label.setColor(Color.LIGHT_GRAY);

    add(label).center().colspan(2);

    row().spaceRight(scale(8));
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(employmentLabel);
    add(gameSpeedLabel);
    add(starRatingLabel);
    starWidget = new NoOpWidget();
    add(starWidget).fill().width(80).height(16);

    starTexture = TowerAssetManager.texture("hud/star.png");
    starTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

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

    return add(label).center();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    lastUpdated += delta;

    if (lastUpdated >= TowerConsts.HUD_UPDATE_FREQUENCY) {
      lastUpdated = 0f;
      Player player = Player.instance();
      starRating = player.getStarRating();

      experienceLabel.setText(formatNumber(player.getExperience()));
      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentExpenses()));
      populationLabel.setText(formatNumber(player.getPopulationResidency()) + "/" + formatNumber(player.getMaxPopulation()));
      employmentLabel.setText(formatNumber(player.getJobsFilled()) + "/" + formatNumber(player.getJobsMax()));
      gameSpeedLabel.setText(towerScene.getTimeMultiplier() + "x");
      starRatingLabel.setText(String.format("%.1f", starRating));

      pack();
    }
  }

  private CharSequence formatNumber(long i) {
    return NumberFormat.getInstance().format(i);
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    return true;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    float starWidth = Math.round(starRating * starTexture.getWidth());
    batch.setColor(Color.WHITE);
    batch.draw(starTexture,
                      x + starWidget.x,
                      y + starWidget.y,
                      starWidth,
                      starWidget.height,
                      0, 0,
                      starWidth / starTexture.getWidth(),
                      -starWidget.height / starTexture.getHeight());
  }
}
