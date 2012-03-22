package com.unhappyrobot;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.unhappyrobot.utils.OSValidator;

public class DesktopGame {
  private DesktopGame() {

  }

  public static void main(String[] args) {

    /*
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      public void uncaughtException(Thread thread, Throwable throwable) {
        StringSelection errorText = new StringSelection(throwable.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(errorText, null);
        JOptionPane.showMessageDialog(null, "An unknown error has occurred:\n\n" + throwable + "\n\n----\nThis message has been copied to your clipboard, please send it to Phil.\n\nThe game will now exit.", "Ooops!", JOptionPane.ERROR_MESSAGE);
        System.exit(100);
      }
    });
    */

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "TowerSim";
    config.resizable = false;
    config.width = 800;
    config.height = 480;
    config.useGL20 = true;
//    config.vSyncEnabled = false;

    LwjglApplicationShim applicationShim = new LwjglApplicationShim(new TowerGame(OSValidator.getOSType(), System.getProperty("os.version")));
    new LwjglApplication(applicationShim, config);
  }
}
