/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.FontManager;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

public class TowerNameBillboard extends GridObject {
  private Sprite sprite;
  private final BitmapFont bitmapFont;
  private Label towerNameLabel;

  public TowerNameBillboard(GridObjectType gridObjectType, GameGrid gameGrid) {
    super(gridObjectType, gameGrid);

    sprite = TowerAssetManager.sprite(TowerAssetManager.WHITE_SWATCH);
    sprite.getTexture().setFilter(Linear, Linear);

    bitmapFont = FontManager.Roboto32.getFont();
    towerNameLabel = FontManager.Roboto32.makeLabel(gameGrid.getTowerName());
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    sprite.setColor(Color.DARK_GRAY);
    sprite.setPosition(worldPosition.x, worldPosition.y);
    sprite.setSize(towerNameLabel.width, towerNameLabel.height);
    sprite.draw(spriteBatch);
    towerNameLabel.x = worldPosition.x;
    towerNameLabel.y = worldPosition.y;
    towerNameLabel.draw(spriteBatch, 1f);
  }
}
