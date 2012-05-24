/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.FontManager;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.happydroids.droidtowers.Strings.wrap;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class TowerNameBillboard extends GameObject {
  private Texture pollTexture;
  private Label towerNameLabel;
  private final NeighborGameGrid gameGrid;
  private final NinePatch billboardArea;

  public TowerNameBillboard(NeighborGameGrid gameGrid) {
    super();
    this.gameGrid = gameGrid;

    pollTexture = TowerAssetManager.texture("tower-sign-poll.png");
    pollTexture.setFilter(Linear, Linear);

    Texture billboardTexture = TowerAssetManager.texture("tower-sign.png");
    billboardTexture.setFilter(Linear, Linear);
    billboardArea = new NinePatch(billboardTexture, 3, 3, 3, 3);

    towerNameLabel = FontManager.BankGothic32.makeLabel(capitalize(wrap(gameGrid.getTowerName(), 16)));
  }

  @Override
  public void draw(SpriteBatch spriteBatch) {
    spriteBatch.setColor(Color.WHITE);
    spriteBatch.draw(pollTexture, getX() + 8 + towerNameLabel.width / 2, getY(), 8, towerNameLabel.height + 100);

    billboardArea.draw(spriteBatch, getX(), getY() + 100, towerNameLabel.width + 32, towerNameLabel.height + 8);

    towerNameLabel.x = getX() + 16;
    towerNameLabel.y = getY() + 4 + 100;
    towerNameLabel.draw(spriteBatch, 1f);
  }
}
