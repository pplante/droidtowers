/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.scenes.TowerScene;

import static com.happydroids.droidtowers.platform.Display.scale;
import static com.happydroids.droidtowers.utils.StringUtils.formatNumber;

public class StatusBarPanel extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float BUTTON_FADE_DURATION = 0.25f;

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
  private StarRatingBar moneyRatingBar;
  private final PopOverMenu ratingOverlay;
  private final NinePatch background;
  private final Texture whiteSwatch;

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
    makeHeader("COINS");
    makeHeader("INCOME");
    makeHeader("EXPENSES");
    makeHeader("POPULATION");
    makeHeader("EMPLOYMENT");
    makeHeader("GAME SPEED");
    makeHeader("STAR RATING");

    row().spaceRight(scale(10));
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(employmentLabel);
    add(gameSpeedLabel);
    add(starWidget);

    moneyRatingBar = new StarRatingBar();

    ratingOverlay = new PopOverMenu();
    ratingOverlay.add(moneyRatingBar);
    ratingOverlay.pack();
    ratingOverlay.visible = false;

    pack();
  }


  private Label makeValueLabel(String labelText) {
    Label label = FontManager.RobotoBold18.makeLabel(labelText);
    label.setAlignment(Align.CENTER);
    return label;
  }

  private Cell makeHeader(String headerText) {
    Label label = FontManager.Roboto12.makeLabel(headerText);
    label.setAlignment(Align.CENTER);
    label.setColor(Colors.ALMOST_BLACK);

    return add(label).center();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    lastUpdated += delta;

    if (lastUpdated >= TowerConsts.HUD_UPDATE_FREQUENCY) {
      lastUpdated = 0f;
      Player player = Player.instance();
      starWidget.setValue(player.getStarRating());

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
    if (hit(x, y) == starWidget) {
      ratingOverlay.visible = !ratingOverlay.visible;

      if (ratingOverlay.visible) {
        ratingOverlay.parent = this.parent;
        getStage().addActor(ratingOverlay);
        ratingOverlay.x = this.x + starWidget.x;
        ratingOverlay.y = this.y - starWidget.y - starWidget.height - ratingOverlay.getOffset();
      }

      action(FadeTo.$(ratingOverlay.visible ? INACTIVE_BUTTON_ALPHA : 1f, BUTTON_FADE_DURATION));
      ratingOverlay.action(FadeTo.$(ratingOverlay.visible ? INACTIVE_BUTTON_ALPHA : 1f, BUTTON_FADE_DURATION));
    }

    System.out.println(ratingOverlay.visible);

    return true;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    batch.setColor(0, 0, 0, color.a - 0.25f);
    batch.draw(whiteSwatch, x, y - 2, width, 2);
    batch.draw(whiteSwatch, x + width, y - 2, 2, height + 4);

    batch.setColor(color);
  }
}
