/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Preferences;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class GdxTestPreferences implements Preferences {
  private HashMap<String, Object> values = Maps.newHashMap();

  public void putBoolean(String key, boolean val) {
    values.put(key, val);
  }

  public void putInteger(String key, int val) {
    values.put(key, val);
  }

  public void putLong(String key, long val) {
    values.put(key, val);
  }

  public void putFloat(String key, float val) {
    values.put(key, val);
  }

  public void putString(String key, String val) {
    values.put(key, val);
  }

  public void put(Map<String, ?> vals) {
    values.putAll(vals);
  }

  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  public int getInteger(String key) {
    return getInteger(key, Integer.MAX_VALUE);
  }

  public long getLong(String key) {
    return getLong(key, Long.MAX_VALUE);
  }

  public float getFloat(String key) {
    return getFloat(key, Float.NaN);
  }

  public String getString(String key) {
    return getString(key, null);
  }

  public boolean getBoolean(String key, boolean defValue) {
    return values.containsKey(key) ? (Boolean) values.get(key) : defValue;
  }

  public int getInteger(String key, int defValue) {
    return values.containsKey(key) ? (Integer) values.get(key) : defValue;
  }

  public long getLong(String key, long defValue) {
    return values.containsKey(key) ? (Long) values.get(key) : defValue;
  }

  public float getFloat(String key, float defValue) {
    return values.containsKey(key) ? (Float) values.get(key) : defValue;
  }

  public String getString(String key, String defValue) {
    return values.containsKey(key) ? (String) values.get(key) : defValue;
  }

  public Map<String, ?> get() {
    return values;
  }

  public boolean contains(String key) {
    return values.containsKey(key);
  }

  public void clear() {
    values.clear();
  }

  public void remove(String key) {
    values.remove(key);
  }

  public void flush() {
  }
}
