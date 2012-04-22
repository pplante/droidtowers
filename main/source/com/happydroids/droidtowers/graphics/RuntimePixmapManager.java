/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.google.common.collect.Lists;

import java.util.List;

public class RuntimePixmapManager {
  private static List<PixmapGenerator> generators = Lists.newArrayList();

  public static void define(PixmapGenerator generator) {

  }

  public static void manage(PixmapGenerator pixmapGenerator) {
    pixmapGenerator.reload();

    generators.add(pixmapGenerator);
  }

  public static void reload() {
    for (PixmapGenerator generator : generators) {
      generator.reload();
    }
  }

  public static void remove(PixmapGenerator pixmapGenerator) {
    generators.remove(pixmapGenerator);
  }
}
