package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;

public class RoomTypeFactory extends GridObjectTypeFactory<RoomType> {
  private static RoomTypeFactory instance;

  private RoomTypeFactory() {
    super(RoomType.class);

    parseTypesFile(Gdx.files.internal("params/rooms.json"));
  }

  public static RoomTypeFactory getInstance() {
    if (instance == null) {
      instance = new RoomTypeFactory();
    }

    return instance;
  }
}
