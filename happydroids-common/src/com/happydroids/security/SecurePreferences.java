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
    Gdx.app.error(TAG, String.format("encrypting: key (%s): %s, value(%s): %s", key, obfuscator.obfuscate(key), val, obfuscator.obfuscate(String.valueOf(val))));
    preferences.putString(obfuscator.obfuscate(key), obfuscator.obfuscate(String.valueOf(val)));
  }

  private String decrypt(String key) {
    try {
      return obfuscator.unobfuscate(preferences.getString(key));
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
    return Boolean.getBoolean(decrypt(key));
  }

  @Override
  public int getInteger(String key) {
    return Integer.parseInt(decrypt(key));
  }

  @Override
  public long getLong(String key) {
    return Long.parseLong(decrypt(key));
  }

  @Override
  public float getFloat(String key) {
    return Float.parseFloat(decrypt(key));
  }

  @Override
  public String getString(String key) {
    return decrypt(key);
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return contains(key) ? Boolean.getBoolean(decrypt(key)) : defValue;
  }

  @Override
  public int getInteger(String key, int defValue) {
    return contains(key) ? Integer.getInteger(decrypt(key)) : defValue;
  }

  @Override
  public long getLong(String key, long defValue) {
    return contains(key) ? Long.getLong(decrypt(key)) : defValue;
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
