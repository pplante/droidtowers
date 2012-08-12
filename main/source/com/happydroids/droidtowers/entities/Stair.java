/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.TransitType;

import java.util.List;

public class Stair extends Transit {
  private Sprite sprite;

  public Stair(TransitType stairType, GameGrid gameGrid) {
    super(stairType, gameGrid);

    TextureAtlas textureAtlas = TowerAssetManager.textureAtlas(stairType.getAtlasFilename());
    sprite = textureAtlas.createSprite("stairs");
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Room || gridObject instanceof Stair;
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch, SpriteCache spriteCache, Color renderTintColor) {
    // stairs need to support overflowing into adjacent cells.
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(position.getWorldX(), position.getWorldY());
      sprite.draw(spriteBatch);
    }
  }

  @Override
  public List<GridPoint> getGridPointsTouched() {
    List<GridPoint> points = super.getGridPointsTouched();
    points.add(new GridPoint(position.x, position.y + 2));
    points.add(new GridPoint(position.x + 1, position.y + 2));

    return points;
  }

  @Override
  public float getDesirability() {
    return 0;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return null;
  }

  @Override
  protected boolean hasPopOver() {
    return false;
  }
}
