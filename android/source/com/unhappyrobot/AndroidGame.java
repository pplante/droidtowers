package com.unhappyrobot;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class AndroidGame extends AndroidApplication {
  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initialize(new TowerGame(), true);
    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
  }
}
