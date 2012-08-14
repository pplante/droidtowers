/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.events.GameSpeedChangeEvent;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.platform.Display.devicePixel;
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
  private final RatingBar starRatingBar;
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;
  private float starRating;
  private final PopOver starRatingPopOver;
  private final Texture whiteSwatch;
  private final PopOver gameSpeedOverlay;
  private final Texture backgroundTexture;
  private final Slider gameSpeedSlider;
  private final Achievement dubai7StarWonder;

  public StatusBarPanel() {
    setTouchable(Touchable.childrenOnly);

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    employmentLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel(SceneManager.activeScene().getTimeMultiplier() + "x");
    starRatingBar = new RatingBar(0, 5);

    whiteSwatch = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
    backgroundTexture = TowerAssetManager.texture("hud/window-bg.png");
    backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    defaults();
    center();

    row().pad(devicePixel(2)).padBottom(0);
    add(makeHeader("COINS", Color.LIGHT_GRAY)).center();
    add(makeHeader("INCOME", Color.LIGHT_GRAY)).center();
    add(makeHeader("EXPENSES", Color.LIGHT_GRAY)).center();
    add(makeHeader("POPULATION", Color.LIGHT_GRAY)).center();
    add(makeHeader("EMPLOYMENT", Color.LIGHT_GRAY)).center();
    Label gameSpeedHeader = makeHeader("GAME SPEED", Color.LIGHT_GRAY);
    add(gameSpeedHeader).center();
    Label starRatingHeader = makeHeader("STAR RATING", Color.LIGHT_GRAY);
    add(starRatingHeader).center();

    row().pad(devicePixel(2)).padTop(0);
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(employmentLabel);
    add(gameSpeedLabel);
    add(starRatingBar);

    if (TowerConsts.ENABLE_NEWS_TICKER) {
      row().pad(devicePixel(2)).padLeft(devicePixel(-4)).padRight(devicePixel(-4));
      add(new HorizontalRule(Colors.ICS_BLUE_SEMI_TRANSPARENT, 1)).fillX().colspan(7);

      row().pad(0);
      add(new NewsTickerPanel()).colspan(7).left();
    }

    dubai7StarWonder = AchievementEngine.instance().findById("dubai-7-star-wonder");

    gameSpeedOverlay = new PopOver();
    gameSpeedOverlay.alignArrow(Align.left);
    gameSpeedOverlay.add(new Image(TowerAssetManager.textureFromAtlas("snail", "hud/buttons.txt"))).center();
    gameSpeedSlider = new Slider(TowerConsts.GAME_SPEED_MIN, TowerConsts.GAME_SPEED_MAX, 0.5f, false, TowerAssetManager.getCustomSkin());
    gameSpeedOverlay.add(gameSpeedSlider).width(devicePixel(150));
    gameSpeedOverlay.add(new Image(TowerAssetManager.textureFromAtlas("rabbit", "hud/buttons.txt"))).center();
    gameSpeedOverlay.pack();
    gameSpeedOverlay.setVisible(false);

    gameSpeedSlider.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        float remainder = gameSpeedSlider.getValue() * 2f / 2f;
        SceneManager.activeScene().setTimeMultiplier(remainder);
      }
    });

    SceneManager.activeScene().events().register(this);

    starRatingPopOver = new TowerRatingPopOver();
    starRatingPopOver.setVisible(false);

    pack();

    VibrateClickListener gameSpeedToggleListener = new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        gameSpeedOverlay.toggle(StatusBarPanel.this, gameSpeedLabel);
      }
    };
    gameSpeedHeader.addListener(gameSpeedToggleListener);
    gameSpeedLabel.addListener(gameSpeedToggleListener);

    VibrateClickListener starRatingListener = new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        starRatingPopOver.toggle(StatusBarPanel.this, starRatingBar);
      }
    };
    starRatingHeader.addListener(starRatingListener);
    starRatingBar.addListener(starRatingListener);

    setTouchable(Touchable.enabled);
    addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {

      }
    });
  }


  private Label makeValueLabel(String labelText) {
    Label label = FontManager.RobotoBold18.makeLabel(labelText);
    label.setAlignment(Align.center);
    return label;
  }

  private Label makeHeader(String headerText, Color tint) {
    Label label = FontManager.Roboto12.makeLabel(headerText);
    label.setAlignment(Align.center);
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
      starRatingBar.setValue(player.getStarRating());

      if (dubai7StarWonder.isCompleted() && starRatingBar.getMaxValue() == 5) {
        starRatingBar.setMaxValue(7);
      }

      experienceLabel.setText(formatNumber(player.getExperience()));

      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + formatNumber(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + formatNumber(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + formatNumber(player.getCurrentExpenses()));
      populationLabel.setText(formatNumber(player.getPopulationResidency()) + "/" + formatNumber(player.getMaxPopulation()));
      employmentLabel.setText(formatNumber(player.getJobsFilled()) + "/" + formatNumber(player.getJobsMax()));

      pack();
    }
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    batch.setColor(Colors.ICS_BLUE_SEMI_TRANSPARENT);
    batch.draw(whiteSwatch, getX(), getY() - LINE_WIDTH, getWidth(), LINE_WIDTH);
    batch.draw(whiteSwatch, getX() + getWidth(), getY() - LINE_WIDTH, LINE_WIDTH, getHeight() + LINE_WIDTH * 2);
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    batch.setColor(getColor());
    batch.draw(backgroundTexture, getX(), getY(), getWidth(), getHeight());
  }

  @Subscribe
  public void TowerScene_onGameSpeedChange(GameSpeedChangeEvent event) {
    gameSpeedSlider.setValue(event.scene.getTimeMultiplier());
    gameSpeedLabel.setText(event.scene.getTimeMultiplier() + "x");
  }
}
