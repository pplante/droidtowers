/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;


import android.app.Activity;
import android.util.DisplayMetrics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.vending.licensing.*;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.gui.ResponseType;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.AndroidBrowserUtil;
import com.happydroids.platform.AndroidConnectionMonitor;
import com.happydroids.platform.AndroidUncaughtExceptionHandler;
import com.happydroids.platform.Platform;

import static android.provider.Settings.Secure.ANDROID_ID;

public class DroidTowerGame extends AndroidApplication {
  public static final String GPL_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjNAsgSYsYinshEWAGLU9WvM7nt3g8nL7EUH2gxymtp6GkPFev3sJwMZaZFNl34/oCRZ0O4DOhEtNxUY+F8WoeKYnXhpzKUvTM7rK8BaOYt3tb4rwjCa1dVhN0Q+NpR4tzMBn5pC1E/p6+4hAJ6v5NZhSpXRPJHhf05MWUqGemUum1fMvl6lCKuox+3FAJ/Cb4QRU4usr/WWDqypYLlEiznsKzMVqFv2F7VR6d1Bdsazw0Rtg+H1Nach26f9rD4ycdqlUyI2LtuGU3OQomO3+zwtLlp/VazoND4mKqVMFTmjb+eh4nxxepncrlenY6NRfmsdmTzg0m63Ed5Ee9Vpq3wIDAQAB";
  public static final byte[] GPL_SALT = "ad076e981c2ea4103f1a6e30b5e8d0bd81bca536".getBytes();

  private HappyDroidLicenseCheckCallback licenseCheckCallback;
  private LicenseChecker licenseChecker;

  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    String deviceId = null;
    AESObfuscator obfuscator = new AESObfuscator(GPL_SALT, getPackageName(), ANDROID_ID);
    ServerManagedPolicy managedPolicy = new ServerManagedPolicy(this, obfuscator);
    licenseChecker = new LicenseChecker(this, managedPolicy, GPL_PUBLIC_KEY);
    licenseCheckCallback = new HappyDroidLicenseCheckCallback(this);

    licenseChecker.checkAccess(licenseCheckCallback);

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    licenseChecker.onDestroy();
  }

  private class HappyDroidLicenseCheckCallback implements LicenseCheckerCallback {
    private final Activity context;

    public HappyDroidLicenseCheckCallback(Activity activity) {
      this.context = activity;
    }

    @Override
    public void allow(int reason) {
      if (isFinishing()) {
        return;
      }
    }

    @Override
    public void dontAllow(int reason) {
      if (isFinishing()) {
        return;
      }

      final Dialog dialog = new Dialog();
      dialog.setTitle("Licensing Error");
      dialog.setCancelable(false);

      if (reason == Policy.RETRY) {
        dialog.setMessage("We were unable to verify the license to Droid Towers.\n\nGoogle Play did not respond, want to try again?")
                .addButton("Retry", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.setCancelable(true);
                    dialog.dismiss();

                    context.runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        licenseChecker.checkAccess(licenseCheckCallback);
                      }
                    });
                  }
                })
                .addButton(ResponseType.NEGATIVE, "Exit", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    Gdx.app.exit();
                  }
                });
      } else {
        dialog.setMessage("We were unable to verify the license to Droid Towers.\n\nYou must purchase the game from the Google Play store to continue.")
                .addButton("Purchase Droid Towers", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.setCancelable(true);
                    dialog.dismiss();
                  }
                });
      }

      Gdx.app.postRunnable(new Runnable() {
        @Override
        public void run() {
          dialog.show();
        }
      });
    }

    @Override
    public void applicationError(int errorCode) {
    }
  }
}
