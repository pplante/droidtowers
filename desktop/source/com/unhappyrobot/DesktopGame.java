package com.unhappyrobot;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
        public static void main (String[] args) {
                new LwjglApplication(new Game(), "Game", 800, 600, false);
        }
}
