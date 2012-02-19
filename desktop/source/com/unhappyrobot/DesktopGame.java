package com.unhappyrobot;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopGame {
  public static void main(String[] args) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "TowerSim";
    config.resizable = true;
    config.width = 800;
    config.height = 600;
    config.useGL20 = true;
//    config.vSyncEnabled = false;

    new LwjglApplication(new TowerGame(), config);
  }
}
