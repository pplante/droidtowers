/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.StringUtils;

import static com.happydroids.droidtowers.TowerAssetManager.sprite;
import static com.happydroids.droidtowers.platform.Display.scale;

public class GridObjectPopOver<T extends GridObject> extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.125f;
  public static final String CONNECTED_TO_TRANSIT = "Connected to Transit";
  public static final String NOT_CONNECTED_TO_TRANSIT = "Disconnected from Transit";
  private static final String COUSIN_VINNIES_HIDEOUT = "Cousin Vinnies Hideout";

  private final Sprite triangle;
  protected final T gridObject;
  private RatingBar desirabilityBar;
  private RatingBar noiseBar;
  protected Label transitLabel;
  private Label cousinVinniesHideout;
  private Label nameLabel;
  private float timeSinceUpdate;
  private Vector3 gridObjectWorldToScreen;
  private Label incomeLabel;
  private Label upkeepLabel;


  public GridObjectPopOver(T gridObject) {
    super();
    this.gridObject = gridObject;
    gridObjectWorldToScreen = new Vector3();

    InputSystem.instance().addInputProcessor(new GridObjectPopOverCloser(this), 10);

    touchable = true;
    triangle = sprite(TowerAssetManager.WHITE_SWATCH_TRIANGLE_LEFT);
    triangle.setColor(Color.DARK_GRAY);

    setBackground(TowerAssetManager.ninePatch("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));
    defaults().left().space(scale(6));

    pad(scale(8));

    row();
    nameLabel = FontManager.RobotoBold18.makeLabel(gridObject.getName());
    add(nameLabel);

    row().fillX().pad(-8).padTop(0).padBottom(0);
    add(new HorizontalRule()).expandX();

    buildControls();
    updateControls();

    setClickListener(new ClickListener() {
      @Override
      public void click(Actor actor, float x, float y) {
      }
    });
  }

  protected void buildControls() {
    row();
    transitLabel = FontManager.Default.makeLabel(CONNECTED_TO_TRANSIT);
    add(transitLabel);
    row();
    cousinVinniesHideout = FontManager.Default.makeLabel(COUSIN_VINNIES_HIDEOUT);
    add(cousinVinniesHideout);

    incomeLabel = FontManager.RobotoBold18.makeLabel("$0");
    upkeepLabel = FontManager.RobotoBold18.makeLabel("$0");

    Table budgetTable = new Table();
    budgetTable.defaults().top().left().space(scale(8));
    budgetTable.row();
    if (gridObject.canEarnMoney()) {
      budgetTable.add(FontManager.Roboto12.makeLabel("INCOME"));
    }
    budgetTable.add(FontManager.Roboto12.makeLabel("COST"));

    budgetTable.row();
    if (gridObject.canEarnMoney()) {
      budgetTable.add(incomeLabel);
    }
    budgetTable.add(upkeepLabel);

    row();
    add(budgetTable).left();

    desirabilityBar = makeStarRatingBar("Desirability");
    noiseBar = makeStarRatingBar("Noise");
    noiseBar.setTextures(RatingBar.NO_SIGN_ICON);
  }

  protected RatingBar makeStarRatingBar(String labelText) {
    row();
    add(FontManager.Roboto12.makeLabel(labelText.toUpperCase()));
    row();
    RatingBar ratingBar = new RatingBar(5f, 5);
    add(ratingBar);

    return ratingBar;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    super.drawBackground(batch, parentAlpha);

    batch.setColor(0.364f, 0.364f, 0.364f, parentAlpha);
    batch.draw(triangle, x - triangle.getWidth() + 1, y + ((height - triangle.getHeight()) / 2));

    batch.setColor(0.2666f, 0.2666f, 0.2666f, parentAlpha);
    batch.draw(triangle, x - triangle.getWidth() + 2, y + ((height - triangle.getHeight()) / 2));
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    timeSinceUpdate -= delta;
    if (timeSinceUpdate <= 0f) {
      timeSinceUpdate = 2f;
      updateControls();
    }

    gridObjectWorldToScreen.set(gridObject.getWorldCenter().x + triangle.getWidth(), gridObject.getWorldCenter().y, 0);
    SceneManager.activeScene().getCamera().project(gridObjectWorldToScreen);
    x = gridObjectWorldToScreen.x;
    y = gridObjectWorldToScreen.y - getPrefHeight() / 2;
  }

  protected void updateControls() {
    boolean updatedData = false;

    if (desirabilityBar != null) {
      desirabilityBar.setValue(gridObject.getDesirability() * 5f);
    }
    if (noiseBar != null) {
      noiseBar.setValue(gridObject.getSurroundingNoiseLevel() * 5f);
    }

    if (transitLabel != null) {
      if (gridObject.isConnectedToTransport() && !transitLabel.getText().equals(CONNECTED_TO_TRANSIT)) {
        transitLabel.setText(CONNECTED_TO_TRANSIT);
        transitLabel.setColor(Color.WHITE);
        updatedData = true;
      } else if (!gridObject.isConnectedToTransport() && !transitLabel.getText().equals(NOT_CONNECTED_TO_TRANSIT)) {
        transitLabel.setText(NOT_CONNECTED_TO_TRANSIT);
        transitLabel.setColor(Color.RED);
        updatedData = true;
      }
    }

    if (cousinVinniesHideout != null) {
      if (gridObject.hasLoanFromCousinVinnie() && !cousinVinniesHideout.visible) {
        cousinVinniesHideout.visible = true;
        getCell(cousinVinniesHideout).ignore(false);
        updatedData = true;
      } else if (!gridObject.hasLoanFromCousinVinnie() && cousinVinniesHideout.visible) {
        cousinVinniesHideout.visible = false;
        getCell(cousinVinniesHideout).ignore(true);
        updatedData = true;
      }
    }

    if (incomeLabel != null) {
      incomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + StringUtils.formatNumber(gridObject.getCoinsEarned()));
    }

    if (upkeepLabel != null) {
      upkeepLabel.setText(TowerConsts.CURRENCY_SYMBOL + StringUtils.formatNumber(gridObject.getUpkeepCost()));
      if (gridObject.canEarnMoney() && gridObject.getCoinsEarned() < gridObject.getUpkeepCost()) {
        upkeepLabel.setColor(Color.RED);
      } else {
        upkeepLabel.setColor(Color.WHITE);
      }
    }

    if (!gridObject.getName().equals(nameLabel.getText())) {
      nameLabel.setText(gridObject.getName());
      updatedData = true;
    }

    if (updatedData) {
      invalidateHierarchy();
      pack();
    }
  }
}
