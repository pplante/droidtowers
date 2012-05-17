/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;

import static com.happydroids.droidtowers.platform.Display.scale;
import static com.happydroids.droidtowers.utils.StringUtils.formatNumber;

public class StatusBarPanel extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.25f;
  public static final int LINE_WIDTH = 1;

  private final TowerScene towerScene;
  private final Label moneyLabel;
  private final Label experienceLabel;
  private final Label gameSpeedLabel;
  private final Label populationLabel;
  private final Label employmentLabel;
  private final Label moneyIncomeLabel;
  private final Label moneyExpensesLabel;
  private final StarRatingBar starWidget;
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;
  private float starRating;
  private StarRatingBar budgetRatingBar;
  private final PopOverMenu ratingOverlay;
  private final NinePatch background;
  private final Texture whiteSwatch;
  private final PopOverMenu gameSpeedOverlay;
  private StarRatingBar desirabilityRatingBar;
  private StarRatingBar populationRatingBar;
  private StarRatingBar employmentRatingBar;

  public StatusBarPanel(TowerScene towerScene) {
    this.towerScene = towerScene;
    touchable = true;

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    employmentLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel("0x");
    starWidget = new StarRatingBar(0, 5);

    whiteSwatch = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
    background = TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.ICS_BLUE_SEMI_TRANSPARENT);
    setBackground(background);

    defaults();
    center();
    pad(scale(4), scale(8), scale(4), scale(8));

    row().spaceRight(scale(10));
    add(makeHeader("COINS", Colors.ALMOST_BLACK)).center();
    add(makeHeader("INCOME", Colors.ALMOST_BLACK)).center();
    add(makeHeader("EXPENSES", Colors.ALMOST_BLACK)).center();
    add(makeHeader("POPULATION", Colors.ALMOST_BLACK)).center();
    add(makeHeader("EMPLOYMENT", Colors.ALMOST_BLACK)).center();
    add(makeHeader("GAME SPEED", Colors.ALMOST_BLACK)).center();
    add(makeHeader("STAR RATING", Colors.ALMOST_BLACK)).center();

    row().spaceRight(scale(10));
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(employmentLabel);
    add(gameSpeedLabel);
    add(starWidget);


    gameSpeedOverlay = new PopOverMenu();
    gameSpeedOverlay.alignArrow(Align.LEFT);
    gameSpeedOverlay.add(new Slider(1f, 4f, 0.5f, TowerAssetManager.getGuiSkin()));
    gameSpeedOverlay.pack();
    gameSpeedOverlay.visible = false;

    budgetRatingBar = new StarRatingBar();
    populationRatingBar = new StarRatingBar();
    employmentRatingBar = new StarRatingBar();
    desirabilityRatingBar = new StarRatingBar();

    ratingOverlay = new PopOverMenu();
    ratingOverlay.row();
    ratingOverlay.add(makeHeader("Monthly Budget", Color.WHITE));
    ratingOverlay.row();
    ratingOverlay.add(budgetRatingBar);
    ratingOverlay.row();
    ratingOverlay.add(makeHeader("Population", Color.WHITE));
    ratingOverlay.row();
    ratingOverlay.add(populationRatingBar);
    ratingOverlay.row();
    ratingOverlay.add(makeHeader("Employment", Color.WHITE));
    ratingOverlay.row();
    ratingOverlay.add(employmentRatingBar);
    ratingOverlay.row();
    ratingOverlay.add(makeHeader("Desirability", Color.WHITE));
    ratingOverlay.row();
    ratingOverlay.add(desirabilityRatingBar);
    ratingOverlay.pack();
    ratingOverlay.visible = false;

    pack();
  }


  private Label makeValueLabel(String labelText) {
    Label label = FontManager.RobotoBold18.makeLabel(labelText);
    label.setAlignment(Align.CENTER);
    return label;
  }

  private Label makeHeader(String headerText, Color tint) {
    Label label = FontManager.Roboto12.makeLabel(headerText);
    label.setAlignment(Align.CENTER);
    label.setColor(tint);

    return label;
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    lastUpdated += delta;

    if (lastUpdated >= TowerConsts.HUD_UPDATE_FREQUENCY) {
      lastUpdated = 0f;
      Player player = Player.instance();
      starWidget.setValue(player.getStarRating());

      budgetRatingBar.setValue(player.getBudgetRating() * 5f);
      populationRatingBar.setValue(player.getPopulationRating() * 5f);
      employmentRatingBar.setValue(player.getEmploymentRating() * 5f);
      desirabilityRatingBar.setValue(player.getDesirabilityRating() * 5f);

      experienceLabel.setText(formatNumber(player.getExperience()));
      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentExpenses()));
      populationLabel.setText(formatNumber(player.getPopulationResidency()) + "/" + formatNumber(player.getMaxPopulation()));
      employmentLabel.setText(formatNumber(player.getJobsFilled()) + "/" + formatNumber(player.getJobsMax()));
      gameSpeedLabel.setText(towerScene.getTimeMultiplier() + "x");

      pack();
    }
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    Actor touched = hit(x, y);

    if (touched == starWidget || touched == gameSpeedLabel) {
      if (touched == starWidget) {
        ratingOverlay.toggle(this, starWidget);
      } else if (touched == gameSpeedLabel) {
        gameSpeedOverlay.toggle(this, gameSpeedLabel);
      }
    }

    return true;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    batch.setColor(0, 0, 0, color.a - 0.25f);
    batch.draw(whiteSwatch, x, y - LINE_WIDTH, width, LINE_WIDTH);
    batch.draw(whiteSwatch, x + width, y - LINE_WIDTH, LINE_WIDTH, height + 4);

    batch.setColor(color);
  }
}
