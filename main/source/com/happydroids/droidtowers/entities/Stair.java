/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;
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
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    // stairs need to support overflowing into adjacent cells.
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(position.getWorldX(gameGrid), position.getWorldY(gameGrid));
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
  public GridPoint getContentSize() {
    GridPoint point = size.cpy();
    point.add(0, 1);
    return point;
  }

  public Vector2 getTopLeftWorldPoint() {
    GridPoint point = new GridPoint(position);
    point.add(0, 1);

    return point.toWorldVector2(gameGrid);
  }

  public Vector2 getBottomRightWorldPoint() {
    GridPoint point = new GridPoint(position);
    point.add(2, 0);

    return point.toWorldVector2(gameGrid);
  }
}
