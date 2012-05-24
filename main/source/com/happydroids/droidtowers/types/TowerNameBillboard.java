/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.FontManager;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

public class TowerNameBillboard extends GridObject {
  private Sprite sprite;
  private final BitmapFont bitmapFont;

  public TowerNameBillboard(GridObjectType gridObjectType, GameGrid gameGrid) {
    super(gridObjectType, gameGrid);

    sprite = TowerAssetManager.sprite(TowerAssetManager.WHITE_SWATCH);
    sprite.getTexture().setFilter(Linear, Linear);

    bitmapFont = FontManager.Roboto32.getFont();
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    sprite.setColor(renderColor);
    sprite.setPosition(worldPosition.x, worldPosition.y);
    sprite.setSize(worldSize.x, worldSize.y);
    sprite.draw(spriteBatch);

    if (gameGrid.getTowerName() != null) {
      bitmapFont.draw(spriteBatch, gameGrid.getTowerName(), worldPosition.x, worldPosition.y);
    }
  }
}
