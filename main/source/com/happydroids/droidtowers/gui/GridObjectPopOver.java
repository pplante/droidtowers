/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.StringUtils;

import java.util.List;

import static com.happydroids.droidtowers.TowerAssetManager.sprite;
import static com.happydroids.droidtowers.gui.FontManager.Default;

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
  private Label needsDroidsLabel;
  private Label cousinVinniesHideout;
  private Label nameLabel;
  private float timeSinceUpdate;
  private Vector3 gridObjectWorldToScreen;
  private Label incomeLabel;
  private Label upkeepLabel;
  private boolean builtControls;
  private List<RatingBar> ratingBars;
  private Table ratingBarContainer;
  private float offsetY;


  public GridObjectPopOver(T gridObject) {
    super();
    this.gridObject = gridObject;
    gridObjectWorldToScreen = new Vector3();
    ratingBars = Lists.newArrayList();
    ratingBarContainer = new Table();

    InputSystem.instance().addInputProcessor(new GridObjectPopOverCloser(this), 0);

    setTouchable(Touchable.enabled);
    triangle = sprite(TowerAssetManager.WHITE_SWATCH_TRIANGLE_LEFT);
    triangle.setColor(Color.DARK_GRAY);

    setBackground(TowerAssetManager.ninePatchDrawable("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));
    defaults().left().space(Display.devicePixel(6));

    pad(Display.devicePixel(8));

    row();
    nameLabel = FontManager.RobotoBold18.makeLabel(gridObject.getName());
    add(nameLabel);

    row().fillX().pad(-8).padTop(0).padBottom(0);
    add(new HorizontalRule()).expandX();

    addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return true;
      }
    });
  }

  protected void buildControls() {
    row();
    transitLabel = Default.makeLabel(CONNECTED_TO_TRANSIT);
    add(transitLabel);

    row();
    needsDroidsLabel = Default.makeLabel("Needs " + (gridObject instanceof CommercialSpace ? "employees" : "residents"), Color.RED);
    add(needsDroidsLabel);

    row();
    cousinVinniesHideout = Default.makeLabel(COUSIN_VINNIES_HIDEOUT);
    add(cousinVinniesHideout);

    incomeLabel = FontManager.RobotoBold18.makeLabel("$0");
    upkeepLabel = FontManager.RobotoBold18.makeLabel("$0");

    Table budgetTable = new Table();
    budgetTable.defaults().top().left().space(Display.devicePixel(8));
    budgetTable.row().fillX();
    if (gridObject.canEarnMoney()) {
      budgetTable.add(FontManager.Roboto12.makeLabel("INCOME"));
    }
    budgetTable.add(FontManager.Roboto12.makeLabel("COST"));

    budgetTable.row();
    if (gridObject.canEarnMoney()) {
      budgetTable.add(incomeLabel).expandX();
    }
    budgetTable.add(upkeepLabel).expandX();

    row();
    add(budgetTable).left();

    row();
    add(ratingBarContainer);

    desirabilityBar = makeStarRatingBar("Desirability");
    noiseBar = makeStarRatingBar("Noise");
    noiseBar.setTextures(RatingBar.NO_SIGN_ICON);
  }

  protected RatingBar makeStarRatingBar(String labelText) {
    RatingBar ratingBar = new RatingBar(5f, 5);
    ratingBar.setUnitLabel(FontManager.Roboto12.makeLabel(labelText, Color.LIGHT_GRAY));
    ratingBars.add(ratingBar);

    return ratingBar;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    super.drawBackground(batch, parentAlpha);

    batch.setColor(0.364f, 0.364f, 0.364f, parentAlpha);
    batch.draw(triangle, getX() - triangle.getWidth() + 1, getY() + ((getHeight() - triangle.getHeight()) / 2) + offsetY);

    batch.setColor(0.2666f, 0.2666f, 0.2666f, parentAlpha);
    batch.draw(triangle, getX() - triangle.getWidth() + 2, getY() + ((getHeight() - triangle.getHeight()) / 2) + offsetY);
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

    if (needsDroidsLabel != null) {
      if (gridObject.needsDroids() && !needsDroidsLabel.isVisible()) {
        needsDroidsLabel.setVisible(true);
        getCell(needsDroidsLabel).ignore(false);
        updatedData = true;
      } else if (!gridObject.needsDroids() && needsDroidsLabel.isVisible()) {
        needsDroidsLabel.setVisible(false);
        getCell(needsDroidsLabel).ignore(true);
        updatedData = true;
      }
    }

    if (cousinVinniesHideout != null) {
      if (gridObject.hasLoanFromCousinVinnie() && !cousinVinniesHideout.isVisible()) {
        cousinVinniesHideout.setVisible(true);
        getCell(cousinVinniesHideout).ignore(false);
        updatedData = true;
      } else if (!gridObject.hasLoanFromCousinVinnie() && cousinVinniesHideout.isVisible()) {
        cousinVinniesHideout.setVisible(false);
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

  @Override
  public void act(float delta) {
    super.act(delta);

    if (!builtControls) {
      builtControls = true;
      buildControls();
      ratingBarContainer.clear();
      ratingBarContainer.defaults().space(Display.devicePixel(4));
      ratingBarContainer.row().fillX();

      for (int i = 0, ratingBarsSize = ratingBars.size(); i < ratingBarsSize; i++) {
        RatingBar ratingBar = ratingBars.get(i);
        if (i % 2 == 0) {
          ratingBarContainer.row().fillX();
        }

        ratingBarContainer.add(ratingBar).expandX().right();
      }
    }

    timeSinceUpdate -= delta;
    if (timeSinceUpdate <= 0f) {
      timeSinceUpdate = 2f;
      updateControls();
    }

    gridObjectWorldToScreen.set(gridObject.getWorldCenter().x + triangle.getWidth(), gridObject.getWorldCenter().y, 0);
    SceneManager.activeScene().getCamera().project(gridObjectWorldToScreen);
    setX(gridObjectWorldToScreen.x);
    float targetY = gridObjectWorldToScreen.y - getPrefHeight() / 2;
    if (targetY < 0) {
      offsetY = targetY;
      targetY = 0;
    }
    setY(targetY);
  }
}
