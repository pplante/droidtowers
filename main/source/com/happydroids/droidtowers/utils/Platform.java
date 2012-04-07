/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import java.lang.reflect.Method;
import java.net.URI;

public class Platform {
  public static void launchWebBrowser(final String uri) {
    try {
      Class<?> d = Class.forName("java.awt.Desktop");
      Method browseMethod = d.getDeclaredMethod("browse", new Class[]{URI.class});
      browseMethod.invoke(d.getDeclaredMethod("getDesktop").invoke(null), URI.create(uri));
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }
}
