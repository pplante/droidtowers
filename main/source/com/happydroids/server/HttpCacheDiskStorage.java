/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.cache.*;
import org.apache.http.impl.client.cache.DefaultHttpCacheEntrySerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpCacheDiskStorage implements HttpCacheStorage {
  private final HttpCacheEntrySerializer serializer;
  private final FileHandle cacheDir;


  public HttpCacheDiskStorage() {
    serializer = new DefaultHttpCacheEntrySerializer();
    cacheDir = GameSaveFactory.getStorageRoot().child("cache/");

    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    }
  }

  @Override
  public void putEntry(String key, HttpCacheEntry entry) throws IOException {
    if (entry.getStatusCode() == 200) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      serializer.writeTo(entry, bos);
      cacheDir.child(getKey(key)).writeBytes(bos.toByteArray(), false);
    }
  }

  private String getKey(String key) {
    return DigestUtils.md5Hex(key);
  }

  @Override
  public HttpCacheEntry getEntry(String key) throws IOException {
    FileHandle file = cacheDir.child(getKey(key));
    if (file.exists()) {
      return serializer.readFrom(new ByteArrayInputStream(file.readBytes()));
    }

    return null;
  }

  @Override
  public void removeEntry(String key) throws IOException {
    FileHandle file = cacheDir.child(getKey(key));
    if (file.exists()) {
      file.delete();
    }
  }

  @Override
  public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
    HttpCacheEntry cacheEntry = getEntry(key);
    HttpCacheEntry updatedEntry = callback.update(cacheEntry);
    putEntry(key, updatedEntry);
  }
}
