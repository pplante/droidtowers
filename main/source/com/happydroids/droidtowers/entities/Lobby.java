/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.RoomType;

public class Lobby extends Room {

  private static final Vector2 TWO_WIDE = new Vector2(2, 1);
  private final Sprite leftCap;
  private final Sprite rightCap;

  public Lobby(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    TextureAtlas atlas = roomType.getTextureAtlas();

    leftCap = new Sprite(atlas.findRegion("lobby-left"));
    leftCap.setOrigin(0, 0);
    leftCap.setScale(getGridScale().x, getGridScale().y);

    rightCap = new Sprite(atlas.findRegion("lobby-right"));
    rightCap.setOrigin(0, 0);
    rightCap.setScale(getGridScale().x, getGridScale().y);
  }

  @Override
  public void updatePopulation() {
    // do nothing!
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    super.render(spriteBatch, renderTintColor);

//TODO: Make this better?
    GridPoint left = getPosition().cpy();
    left.sub(2, 0);

    Vector2 gridScale = getGridScale();
    if (gameGrid.positionCache().getObjectsAt(left, TWO_WIDE).size() == 0) {
      leftCap.setColor(renderColor);
      leftCap.setPosition(worldPosition.x - (leftCap.getWidth() * gridScale.x), worldPosition.y);
      leftCap.draw(spriteBatch);
    }

    GridPoint right = getPosition().cpy();
    right.add(size.x, 0);

    if (gameGrid.positionCache().getObjectsAt(right, TWO_WIDE).size() == 0) {
      rightCap.setColor(renderColor);
      rightCap.setOrigin(0, 0);
      rightCap.setPosition(worldPosition.x + worldSize.x, worldPosition.y);
      rightCap.setScale(gridScale.x, gridScale.y);
      rightCap.draw(spriteBatch);
    }
  }
}
