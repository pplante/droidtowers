/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.security;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.happydroids.HappyDroidConsts;

import java.util.Map;

public class SecurePreferences implements Preferences {
  private static final String TAG = SecurePreferences.class.getSimpleName();

  private final Preferences preferences;
  private final AESObfuscator obfuscator;

  public SecurePreferences(String bucketName) {
    obfuscator = new AESObfuscator(HappyDroidConsts.OBFUSCATION_SALT, HappyDroidConsts.OBFUSCATION_KEY);
    preferences = Gdx.app.getPreferences(bucketName);
  }

  private void encrypt(String key, Object val) {
    preferences.putString(obfuscator.obfuscate(key), obfuscator.obfuscate(obfuscator.obfuscate(key) + String.valueOf(val)));
  }

  private String decrypt(String key) {
    try {
      String obfuscatedKey = obfuscator.obfuscate(key);
      return obfuscator.unobfuscate(preferences.getString(obfuscatedKey)).replace(obfuscatedKey, "");
    } catch (AESObfuscator.ValidationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void putBoolean(String key, boolean val) {
    encrypt(key, val);
  }

  @Override
  public void putInteger(String key, int val) {
    encrypt(key, val);

  }

  @Override
  public void putLong(String key, long val) {
    encrypt(key, val);
  }

  @Override
  public void putFloat(String key, float val) {
    encrypt(key, val);
  }

  @Override
  public void putString(String key, String val) {
    encrypt(key, val);
  }

  @Override
  public void put(Map<String, ?> values) {
    for (Map.Entry<String, ?> entry : values.entrySet()) {
      encrypt(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  @Override
  public int getInteger(String key) {
    return getInteger(key, 0);
  }

  @Override
  public long getLong(String key) {
    return getLong(key, 0);
  }

  @Override
  public float getFloat(String key) {
    return getFloat(key, 0f);
  }

  @Override
  public String getString(String key) {
    return getString(key, null);
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return contains(key) ? Boolean.parseBoolean(decrypt(key)) : defValue;
  }

  @Override
  public int getInteger(String key, int defValue) {
    return contains(key) ? Integer.parseInt(decrypt(key)) : defValue;
  }

  @Override
  public long getLong(String key, long defValue) {
    return contains(key) ? Long.parseLong(decrypt(key)) : defValue;
  }

  @Override
  public float getFloat(String key, float defValue) {
    return contains(key) ? Float.parseFloat(decrypt(key)) : defValue;
  }

  @Override
  public String getString(String key, String defValue) {
    return contains(key) ? decrypt(key) : defValue;
  }

  @Override
  public Map<String, ?> get() {
    throw new RuntimeException("Not supported.");
  }

  @Override
  public boolean contains(String key) {
    return preferences.contains(obfuscator.obfuscate(key));
  }

  @Override
  public void clear() {
    preferences.clear();
  }

  @Override
  public void remove(String key) {
    preferences.remove(obfuscator.obfuscate(key));
  }

  @Override
  public void flush() {
    preferences.flush();
  }
}
