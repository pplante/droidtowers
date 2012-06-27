/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;


import android.util.DisplayMetrics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.AndroidBrowserUtil;
import com.happydroids.platform.AndroidConnectionMonitor;
import com.happydroids.platform.AndroidUncaughtExceptionHandler;
import com.happydroids.platform.Platform;

public class DroidTowerGame extends AndroidApplication {
  public static final String GPL_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjNAsgSYsYinshEWAGLU9WvM7nt3g8nL7EUH2gxymtp6GkPFev3sJwMZaZFNl34/oCRZ0O4DOhEtNxUY+F8WoeKYnXhpzKUvTM7rK8BaOYt3tb4rwjCa1dVhN0Q+NpR4tzMBn5pC1E/p6+4hAJ6v5NZhSpXRPJHhf05MWUqGemUum1fMvl6lCKuox+3FAJ/Cb4QRU4usr/WWDqypYLlEiznsKzMVqFv2F7VR6d1Bdsazw0Rtg+H1Nach26f9rD4ycdqlUyI2LtuGU3OQomO3+zwtLlp/VazoND4mKqVMFTmjb+eh4nxxepncrlenY6NRfmsdmTzg0m63Ed5Ee9Vpq3wIDAQAB";
  public static final byte[] GPL_SALT = "ad076e981c2ea4103f1a6e30b5e8d0bd81bca536".getBytes();

  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Platform.setConnectionMonitor(new AndroidConnectionMonitor(this));
    Platform.setUncaughtExceptionHandler(new AndroidUncaughtExceptionHandler(this));
    Platform.setBrowserUtil(new AndroidBrowserUtil(this));

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    Display.setHDPI(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH);
    Display.setScaledDensity(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? 1.5f : 1f);

    TowerGameService.setDeviceOSName("android");
    TowerGameService.setDeviceOSVersion("sdk" + getVersion());

    initialize(new TowerGame(), true);

    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
  }
}
