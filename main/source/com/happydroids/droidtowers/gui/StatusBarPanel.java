/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.events.GameSpeedChangeEvent;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.platform.Display.scale;
import static com.happydroids.droidtowers.utils.StringUtils.formatNumber;

public class StatusBarPanel extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.25f;
  public static final int LINE_WIDTH = 2;

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
  private final PopOverLayer ratingOverlay;
  private final Texture whiteSwatch;
  private final PopOverLayer gameSpeedOverlay;
  private StarRatingBar desirabilityRatingBar;
  private StarRatingBar populationRatingBar;
  private StarRatingBar employmentRatingBar;
  private final Texture backgroundTexture;
  private final Slider gameSpeedSlider;
  private final Achievement dubai7StarWonder;

  public StatusBarPanel() {
    touchable = true;

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    employmentLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel(SceneManager.activeScene().getTimeMultiplier() + "x");
    starWidget = new StarRatingBar(0, 5);

    whiteSwatch = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
    backgroundTexture = TowerAssetManager.texture("hud/window-bg.png");
    backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    defaults();
    center();
    pad(scale(4), scale(8), scale(4), scale(8));

    row().spaceRight(scale(10));
    add(makeHeader("COINS", Color.LIGHT_GRAY)).center();
    add(makeHeader("INCOME", Color.LIGHT_GRAY)).center();
    add(makeHeader("EXPENSES", Color.LIGHT_GRAY)).center();
    add(makeHeader("POPULATION", Color.LIGHT_GRAY)).center();
    add(makeHeader("EMPLOYMENT", Color.LIGHT_GRAY)).center();
    add(makeHeader("GAME SPEED", Color.LIGHT_GRAY)).center();
    add(makeHeader("STAR RATING", Color.LIGHT_GRAY)).center();

    row().spaceRight(scale(10));
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(employmentLabel);
    add(gameSpeedLabel);
    add(starWidget);

    dubai7StarWonder = AchievementEngine.instance().findById("dubai-7-star-wonder");

    gameSpeedOverlay = new PopOverLayer();
    gameSpeedOverlay.alignArrow(Align.LEFT);
    gameSpeedOverlay.add(new Image(TowerAssetManager.textureFromAtlas("snail", "hud/buttons.txt"))).center();
    gameSpeedSlider = new Slider(0.5f, 4f, 0.5f, TowerAssetManager.getCustomSkin());
    gameSpeedOverlay.add(gameSpeedSlider).width(scale(150));
    gameSpeedOverlay.add(new Image(TowerAssetManager.textureFromAtlas("rabbit", "hud/buttons.txt"))).center();
    gameSpeedOverlay.pack();
    gameSpeedOverlay.visible = false;

    gameSpeedSlider.setValueChangedListener(new Slider.ValueChangedListener() {
      @Override
      public void changed(Slider slider, float value) {
        float remainder = value * 2f / 2f;
        SceneManager.activeScene().setTimeMultiplier(remainder);
      }
    });

    SceneManager.activeScene().events().register(this);

    budgetRatingBar = new StarRatingBar();
    populationRatingBar = new StarRatingBar();
    employmentRatingBar = new StarRatingBar();
    desirabilityRatingBar = new StarRatingBar();

    ratingOverlay = new PopOverLayer();
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

      if (dubai7StarWonder.isCompleted() && starWidget.getMaxStars() == 5) {
        starWidget.setMaxStars(7);
      }

      budgetRatingBar.setValue(player.getBudgetRating() * 5f);
      populationRatingBar.setValue(player.getPopulationRating() * 5f);
      employmentRatingBar.setValue(player.getEmploymentRating() * 5f);
      desirabilityRatingBar.setValue(player.getDesirabilityRating() * 5f);

      experienceLabel.setText(formatNumber(player.getExperience()));
      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentExpenses()));
      populationLabel.setText(formatNumber(player.getPopulationResidency()));
      employmentLabel.setText(formatNumber(player.getJobsFilled()));

      pack();
    }
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    Actor touched = hit(x, y);

    if (touched == starWidget || touched == gameSpeedLabel) {
      Gdx.input.vibrate(15);
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

    batch.setColor(Colors.ICS_BLUE_SEMI_TRANSPARENT);
    batch.draw(whiteSwatch, x, y - LINE_WIDTH, width, LINE_WIDTH);
    batch.draw(whiteSwatch, x + width, y - LINE_WIDTH, LINE_WIDTH, height + LINE_WIDTH * 2);

    batch.setColor(color);
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    batch.draw(backgroundTexture, x, y, width, height);
  }

  @Subscribe
  public void TowerScene_onGameSpeedChange(GameSpeedChangeEvent event) {
    gameSpeedSlider.setValue(event.scene.getTimeMultiplier());
    gameSpeedLabel.setText(event.scene.getTimeMultiplier() + "x");
  }
}
