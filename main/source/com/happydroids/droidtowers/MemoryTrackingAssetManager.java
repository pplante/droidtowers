/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import static com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;

public class MemoryTrackingAssetManager extends AssetManager {
  private int currentMemory;
  private Map<String, Integer> memoryPerFile;

  public MemoryTrackingAssetManager() {
    super();

    currentMemory = 0;
    memoryPerFile = new HashMap<String, Integer>();
  }

  @SuppressWarnings("unchecked")
  private int calculateTextureSize(AssetManager assetManager, String fileName, Class type) {
    if (memoryPerFile.containsKey(fileName)) {
      return memoryPerFile.get(fileName);
    }

    Texture texture = (Texture) assetManager.get(fileName, type);
    TextureData textureData = texture.getTextureData();
    int textureSize = textureData.getWidth() * textureData.getHeight();
    switch (textureData.getFormat()) {
      case RGB565:
        textureSize *= 2;
        break;
      case RGB888:
        textureSize *= 3;
        break;
      case RGBA4444:
        textureSize *= 2;
        break;
      case RGBA8888:
        textureSize *= 4;
        break;
    }

    if (textureData.useMipMaps()) {
      textureSize *= 1.33f;
    }

    memoryPerFile.put(fileName, textureSize);

    return textureSize;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
    if (type.equals(Texture.class)) {
      if (parameter == null) {
        parameter = (AssetLoaderParameters<T>) new TextureParameter();
      }

      final LoadedCallback prevCallback = parameter.loadedCallback;
      parameter.loadedCallback = new LoadedCallback() {
        public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
          if (prevCallback != null) {
            prevCallback.finishedLoading(assetManager, fileName, type);
          }

          currentMemory += calculateTextureSize(assetManager, fileName, type);
        }
      };

    }

    super.load(fileName, type, parameter);
  }

  @Override
  public synchronized void unload(String fileName) {
    super.unload(fileName);

    if (memoryPerFile.containsKey(fileName)) {
      currentMemory -= memoryPerFile.get(fileName);
    }
  }

  public float getMemoryInMegabytes() {
    return (float) currentMemory / 1024f / 1024f;
  }
}
