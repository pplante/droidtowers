/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.security;

import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.error.ErrorUtil;
import com.happydroids.jackson.HappyDroidObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class SecurePreferences {
  private HashMap<String, String> values;
  private FileHandle storage;
  private HappyDroidObjectMapper mapper;

  public SecurePreferences(String fileName) {
    try {
      mapper = TowerGameService.instance().getObjectMapper();
      storage = GameSaveFactory.getStorageRoot().child(fileName);
      if (storage.exists()) {
        values = mapper.readValue(storage.readBytes(), mapper.getTypeFactory()
                                                               .constructMapType(HashMap.class, String.class, String.class));
      } else {
        values = Maps.newHashMap();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public synchronized boolean contains(String key) {
    return values.containsKey(key);
  }

  public synchronized void putString(String key, String value) {
    if (key != null) {
      values.put(key, value);
    } else {
      ErrorUtil.rethrowError(new RuntimeException("putString() requires a non null key!"));
    }
  }

  public synchronized void flush() {
    try {
      storage.writeBytes(mapper.writeValueAsBytes(values), false);
    } catch (IOException e) {
      ErrorUtil.rethrowError(e);
    }
  }

  public synchronized String getString(String key, String defautValue) {
    if (!values.containsKey(key)) {
      return defautValue;
    }

    return getString(key);
  }

  public synchronized String getString(String key) {
    return values.get(key);
  }

  public synchronized void remove(String key) {
    values.remove(key);
  }

  public synchronized void putBoolean(String key, boolean value) {
    values.put(key, String.valueOf(value));
  }

  public synchronized boolean getBoolean(String key, boolean defaultValue) {
    if (!values.containsKey(key)) {
      return defaultValue;
    }

    return getBoolean(key);
  }

  public synchronized int getInteger(String key) {
    return Integer.parseInt(values.get(key));
  }

  public synchronized boolean getBoolean(String key) {
    return Boolean.parseBoolean(values.get(key));
  }

  public synchronized void putInteger(String key, int value) {
    values.put(key, String.valueOf(value));
  }

  public synchronized int incrementInt(String key) {
    int integer = getInteger(key, 0) + 1;
    putInteger(key, integer);

    return integer;
  }

  private synchronized int getInteger(String key, int defaultValue) {
    if (!values.containsKey(key)) {
      return defaultValue;
    }

    return Integer.parseInt(values.get(key));
  }

  public void putFloat(String key, float value) {
    values.put(key, String.valueOf(value));
  }

  public float getFloat(String key, float defaultValue) {
    if (!values.containsKey(key)) {
      return defaultValue;
    }

    return Float.parseFloat(values.get(key));
  }
}
