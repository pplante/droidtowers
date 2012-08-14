/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pipeline;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apach3.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class AssetList {
  public final Map<String, Class> preloadFiles;
  public final Map<String, Class> normalFiles;
  public final Map<String, String> highDefFiles;

  public final List<String> musicFiles;

  public AssetList() {
    preloadFiles = Maps.newHashMap();
    normalFiles = Maps.newHashMap();
    highDefFiles = Maps.newHashMap();
    musicFiles = Lists.newArrayList();
  }

  public void preload(String fileName, String hdVersion, Class clazz) {
    preloadFiles.put(fileName, clazz);

    normalFiles.remove(fileName);

    if (hdVersion != null && !StringUtils.isEmpty(hdVersion)) {
      highDefFiles.put(fileName, hdVersion);
    }
  }

  public void normal(String fileName, String hdVersion, Class clazz) {
    if (!preloadFiles.containsKey(fileName)) {
      normalFiles.put(fileName, clazz);

      if (hdVersion != null && !StringUtils.isEmpty(hdVersion)) {
        highDefFiles.put(fileName, hdVersion);
      }
    }
  }

  public void addMusic(String fileName) {
    musicFiles.add(fileName);
  }
}
