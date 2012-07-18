/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.security;

import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.jackson.HappyDroidObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class SecurePreferences {
  private final HashMap<String, String> values;
  private final FileHandle storage;
  private final HappyDroidObjectMapper mapper;

  public SecurePreferences(String fileName) {
    try {
      mapper = TowerGameService.instance().getObjectMapper();
      storage = GameSaveFactory.getStorageRoot().child(fileName);
      if (storage.exists()) {
        values = mapper.readValue(storage.readBytes(), mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class));
      } else {
        values = Maps.newHashMap();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean contains(String key) {
    return values.containsKey(key);
  }

  public void putString(String key, String value) {
    if (key != null) {
      values.put(key, value);
    }
  }

  public void flush() {
    try {
      mapper.writeValue(storage.file(), values);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getString(String key, String defautValue) {
    if (!values.containsKey(key)) {
      return defautValue;
    }

    return getString(key);
  }

  public String getString(String key) {
    return values.get(key);
  }

  public void remove(String key) {
    values.remove(key);
  }

  public void putBoolean(String key, boolean value) {
    values.put(key, String.valueOf(value));
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    if (!values.containsKey(key)) {
      return defaultValue;
    }

    return getBoolean(key);
  }

  public int getInteger(String key) {
    return Integer.parseInt(values.get(key));
  }

  public boolean getBoolean(String key) {
    return Boolean.parseBoolean(values.get(key));
  }

  public void putInteger(String key, int value) {
    values.put(key, String.valueOf(value));
  }

  public int incrementInt(String key) {
    int integer = getInteger(key, 0) + 1;
    putInteger(key, integer);

    return integer;
  }

  private int getInteger(String key, int defaultValue) {
    if (!values.containsKey(key)) {
      return defaultValue;
    }

    return Integer.parseInt(values.get(key));
  }
}
