package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Lists;
import com.unhappyrobot.entities.RoomType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeFactory {
  private static RoomTypeFactory instance;
  private List<RoomType> roomTypes;

  private RoomTypeFactory() {
    roomTypes = Lists.newArrayList();

    FileHandle fileHandle = Gdx.files.internal("rooms/generic.json");
    ObjectMapper mapper = new ObjectMapper();
    try {
      roomTypes = mapper.readValue(fileHandle.reader(), TypeFactory.collectionType(ArrayList.class, RoomType.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static RoomTypeFactory getInstance() {
    if (instance == null) {
      instance = new RoomTypeFactory();
    }

    return instance;
  }

  public static List<RoomType> all() {
    return getInstance().roomTypes;
  }
}
