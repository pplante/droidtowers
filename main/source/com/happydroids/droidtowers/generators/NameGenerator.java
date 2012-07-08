/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.utils.Random;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.happydroids.droidtowers.types.ProviderType.*;

public class NameGenerator {
  private static List<String> placeNames;
  private static List<String> corporationNames;
  private static List<String> femaleNames;

  public static void initialize() {
    placeNames = parseFile("generators/places.txt");
    corporationNames = parseFile("generators/corporations.txt");
    femaleNames = parseFile("generators/names-female.txt");
  }

  private static List<String> parseFile(String fileName) {
    FileHandle placesFile = Gdx.files.internal(fileName);
    String[] placesContent = placesFile.readString().split("\n");

    List<String> uniqueLines = Lists.newArrayList();
    for (String placeName : placesContent) {
      if (StringUtils.isEmpty(placeName)) continue;
      uniqueLines.add(placeName);
    }

    return uniqueLines;
  }

  private static String randomEntry(List<String> stringList) {
    return stringList.get(Random.randomInt(stringList.size() - 1));
  }

  public static String randomCorporationName() {
    return randomEntry(corporationNames);
  }

  public static String randomFoodServiceName() {
    return randomEntry(placeNames);
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
    return randomEntry(femaleNames);
  }
}
