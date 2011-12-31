package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.types.RoomType;

public class Room extends GridObject {
  private static TextureAtlas roomAtlas;
  private Sprite sprite;

  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    if (roomAtlas == null) {
      roomAtlas = new TextureAtlas(Gdx.files.internal(roomType.getAtlas()));
    }

    sprite = new Sprite(roomAtlas.findRegion(roomType.getImage()));

    size.set(roomType.getWidth(), roomType.getHeight());
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public boolean canShareSpace() {
    return false;
  }
}
