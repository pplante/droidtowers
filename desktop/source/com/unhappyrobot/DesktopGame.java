package com.unhappyrobot;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
  public static void main(String[] args) {
    new LwjglApplication(new TowerGame(), "TowerGame", 800, 600, false);
  }
}
