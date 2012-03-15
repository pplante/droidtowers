package com.unhappyrobot.utils;

public class OSValidator {
  private OSValidator() {

  }

  public static String getOSType() {
    if (isMac())
      return "macosx";

    if (isWindows())
      return "windows";

    if (isUnix())
      return "linux";

    return "unknown";
  }

  public static boolean isWindows() {

    String os = System.getProperty("os.name").toLowerCase();
    // windows
    return (os.indexOf("win") >= 0);

  }

  public static boolean isMac() {

    String os = System.getProperty("os.name").toLowerCase();
    // Mac
    return (os.indexOf("mac") >= 0);

  }

  public static boolean isUnix() {

    String os = System.getProperty("os.name").toLowerCase();
    // linux or unix
    return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) || (os.indexOf("sunos") >= 0);

  }
}
