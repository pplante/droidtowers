/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.happydroids.HappyDroidConsts;

import java.io.IOException;

public class ClassNameResolver {
  public static final String[] packageNames = new String[]{"",
                                                                  "com.happydroids.server",
                                                                  "com.happydroids.droidtowers.entities",
                                                                  "com.happydroids.droidtowers.types",
                                                                  "com.happydroids.droidtowers.unhappyrobot",
                                                                  "com.happydroids.droidtowers.gamestate",
                                                                  "com.unhappyrobot.entities",
                                                                  "com.unhappyrobot.types",
                                                                  "com.unhappyrobot.gamestate",
                                                                  "com.unhappyrobot.gamestate.server"
  };

  public static Class tryToLoadClass(String className) throws IOException {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("Looking for: " + className);
    }
    try {
      return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public static Class resolveClass(String missingClassName) throws IOException {
    if (missingClassName.contains(".")) {
      Class clazz = tryToLoadClass(missingClassName);
      if (clazz != null) {
        return clazz;
      }

      // strip any packages since we're sort of at a last ditch effort here..
      missingClassName = missingClassName.substring(missingClassName.lastIndexOf(".") + 1);
    }

    for (String packageName : packageNames) {
      Class clazz = tryToLoadClass(String.format("%s.%s", packageName, missingClassName));
      if (clazz != null) {
        return clazz;
      }
    }

    throw new RuntimeException("Cannot find class: " + missingClassName);
  }
}
