package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;

public class ServiceRoomTypeFactory extends GridObjectTypeFactory<ServiceRoomType> {
  private static ServiceRoomTypeFactory instance;

  private ServiceRoomTypeFactory() {
    super(ServiceRoomType.class);

    parseTypesFile(Gdx.files.internal("params/services.json"));
  }

  public static ServiceRoomTypeFactory getInstance() {
    if (instance == null) {
      instance = new ServiceRoomTypeFactory();
    }

    return instance;
  }
}
