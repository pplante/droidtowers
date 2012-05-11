/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pipeline;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.Maps;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class AssetList {
  public final Map<String, Class> preloadFiles;
  public final Map<String, Class> normalFiles;
  public final Map<String, String> highDefFiles;

  public AssetList() {
    preloadFiles = Maps.newHashMap();
    normalFiles = Maps.newHashMap();
    highDefFiles = Maps.newHashMap();
  }

  public void preload(String fileName, String hdVersion, Class clazz) {
    preloadFiles.put(fileName, clazz);

    if (hdVersion != null && !hdVersion.isEmpty()) {
      highDefFiles.put(fileName, hdVersion);
    }
  }

  public void normal(String fileName, String hdVersion, Class clazz) {
    normalFiles.put(fileName, clazz);

    if (hdVersion != null && !hdVersion.isEmpty()) {
      highDefFiles.put(fileName, hdVersion);
    }
  }
}
