package com.unhappyrobot;


import android.os.Build;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.unhappyrobot.gamestate.server.HappyDroidService;

public class DroidTowerGame extends AndroidApplication {
  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    HappyDroidService.setDeviceOSName("android");
    HappyDroidService.setDeviceOSVersion("sdk" + Build.VERSION.SDK_INT);

    initialize(new TowerGame(), true);
    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
  }
}
