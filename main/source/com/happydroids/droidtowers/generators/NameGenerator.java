/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.types.GridObjectType;
import org.apach3.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.happydroids.droidtowers.types.ProviderType.*;

public class NameGenerator {
  private static Iterator<String> placeNames;
  private static Iterator<String> corporationNames;
  private static Iterator<String> femaleNames;
  private static Iterator<String> maleNames;


  public static void initialize() {
    placeNames = Iterables.cycle(parseFile("generators/places.txt")).iterator();
    corporationNames = Iterables.cycle(parseFile("generators/corporations.txt")).iterator();
    femaleNames = Iterables.cycle(parseFile("generators/names-female.txt")).iterator();
    maleNames = Iterables.cycle(parseFile("generators/names-male.txt")).iterator();
  }

  private static List<String> parseFile(String fileName) {
    FileHandle placesFile = Gdx.files.internal(fileName);
    String[] placesContent = placesFile.readString().split("\n");

    List<String> uniqueLines = Lists.newArrayList();
    for (String placeName : placesContent) {
      if (StringUtils.isEmpty(placeName)) {
        continue;
      }
      uniqueLines.add(placeName);
    }

    Collections.shuffle(uniqueLines);

    return uniqueLines;
  }

  public static String randomCorporationName() {
    return corporationNames.next();
  }

  public static String randomFoodServiceName() {
    return placeNames.next();
  }

  public static String randomNameForGridObjectType(GridObjectType gridObjectType) {
    if (gridObjectType.provides(OFFICE_SERVICES)) {
      return randomCorporationName();
    } else if (gridObjectType.provides(FOOD, ENTERTAINMENT)) {
      return randomFoodServiceName();
    }

    return null;
  }

  public static String randomFemaleName() {
    return femaleNames.next();
  }

  public static String randomMaleName() {
    return maleNames.next();
  }
}
