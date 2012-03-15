package com.unhappyrobot;


import android.os.Build;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class DroidTowerGame extends AndroidApplication {
  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initialize(new TowerGame("android", "sdk" + Build.VERSION.SDK_INT), true);
    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
  }
}
