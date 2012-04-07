/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPositionCache;
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
    rightCap = new Sprite(atlas.findRegion("lobby-right"));
  }

  @Override
  public void updatePopulation() {
    // do nothing!
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    super.render(spriteBatch);
//TODO: Make this better?
    GridPoint left = getPosition().cpy();
    left.sub(2, 0);

    if (GridPositionCache.instance().getObjectsAt(left, TWO_WIDE).size() == 0) {
      leftCap.setPosition(getSprite().getX() - leftCap.getWidth(), left.getWorldY(gameGrid));
      leftCap.draw(spriteBatch);
    }

    GridPoint right = getPosition().cpy();
    right.add(size.x, 0);

    if (GridPositionCache.instance().getObjectsAt(right, TWO_WIDE).size() == 0) {
      rightCap.setPosition(right.getWorldX(gameGrid), right.getWorldY(gameGrid));
      rightCap.draw(spriteBatch);
    }
  }
}
