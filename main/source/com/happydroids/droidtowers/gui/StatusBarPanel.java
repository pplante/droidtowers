/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
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
  private float lastUpdated = TowerConsts.HUD_UPDATE_FREQUENCY;
  private final Label moneyIncomeLabel;
  private final Label moneyExpensesLabel;
  private final NoOpWidget starWidget;
  private final Sprite starSprite;

  public StatusBarPanel(TowerScene towerScene) {
    this.towerScene = towerScene;

    moneyLabel = makeValueLabel("0");
    moneyIncomeLabel = makeValueLabel("0");
    moneyExpensesLabel = makeValueLabel("0");
    experienceLabel = makeValueLabel("0");
    populationLabel = makeValueLabel("0");
    gameSpeedLabel = makeValueLabel("0x");

    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults();
    center();
    pad(scale(4), scale(8), scale(4), scale(8));

    row().spaceRight(scale(8));
    makeHeader("COINS");
    makeHeader("INCOME");
    makeHeader("EXPENSES");
    makeHeader("POPULATION");
    makeHeader("GAME SPEED");
    makeHeader("RATING");

    row().spaceRight(scale(8));
    add(moneyLabel);
    add(moneyIncomeLabel);
    add(moneyExpensesLabel);
    add(populationLabel);
    add(gameSpeedLabel);
    starWidget = new NoOpWidget();
    add(starWidget).fill().width(80).height(16);
    starSprite = TowerAssetManager.sprite("hud/star.png");
    starSprite.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    pack();
//    debug();
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
      experienceLabel.setText(formatNumber(player.getExperience()));
      moneyLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCoins()));
      moneyIncomeLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentIncome()));
      moneyExpensesLabel.setText(TowerConsts.CURRENCY_SYMBOL + " " + formatNumber(player.getCurrentExpenses()));
      populationLabel.setText(formatNumber(player.getTotalPopulation()) + "/" + formatNumber(player.getMaxPopulation()));

      gameSpeedLabel.setText(towerScene.getTimeMultiplier() + "x");

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
    Vector2 point = new Vector2();
    starWidget.toLocalCoordinates(point);
    Widget.toScreenCoordinates(starWidget, point);
    starSprite.setX(starWidget.x + x);
    starSprite.setY(starWidget.y + y);
    starSprite.setSize(starWidget.width, starWidget.height);
    starSprite.setU2(starWidget.width / starSprite.getTexture().getWidth());
    starSprite.setV2(starWidget.height / starSprite.getTexture().getHeight());
    starSprite.draw(batch);
  }
}
