/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;

public class FontManager {
  public static FontHelper Default = new FontHelper("fonts/Roboto-Regular.ttf", 16, 8, 16);
  public static FontHelper Roboto12 = new FontHelper("fonts/Roboto-Regular.ttf", 14, 8, 16);
  public static FontHelper Roboto18 = new FontHelper("fonts/Roboto-Regular.ttf", 18, 8, 16);
  public static FontHelper RobotoBold18 = new FontHelper("fonts/Roboto-Bold.ttf", 18, 8, 16);
  public static FontHelper Roboto24 = new FontHelper("fonts/Roboto-Regular.ttf", 24, 8, 16);
  public static FontHelper Roboto32 = new FontHelper("fonts/Roboto-Regular.ttf", 32, 16, 326);
  public static FontHelper Roboto64 = new FontHelper("fonts/Roboto-Regular.ttf", 64, 16, 32);
  public static FontHelper BankGothic32 = new FontHelper("fonts/bank_gothic_32.fnt", Color.WHITE);

  public static void resetAll() {
    Default.dispose();
    Roboto12.dispose();
    Roboto18.dispose();
    RobotoBold18.dispose();
    Roboto24.dispose();
    Roboto32.dispose();
    Roboto64.dispose();
    Roboto32.dispose();
    Roboto64.dispose();
    BankGothic32.dispose();
  }
}
