/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.HappyDroidsDesktopUncaughtExceptionHandler;
import com.happydroids.sparky.platform.PlatformProtocolHandlerFactory;
import com.happydroids.utils.BackgroundTask;
import jodd.util.ClassLoaderUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SparkyMain extends JFrame {
  private JPanel contentPane;
  private JLabel happyDroidsLogo;
  private JProgressBar updateProgress;
  private JEditorPane gameUpdates;
  private JLabel updateStatus;
  private JPanel innerPane;
  private JButton startBuildingButton;
  private boolean updateInProgress;
  private File gameStorage;
  private File gameJar;

  public SparkyMain() {
    setContentPane(contentPane);
    setSize(800, 480);
    setResizable(false);
    setTitle("Droid Towers");
    getRootPane().setDefaultButton(startBuildingButton);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    startBuildingButton.addActionListener(new StartBuildingButtonClick());

// call onCancel() when cross is clicked
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel(e);
      }
    });

// call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel(null);
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    updateProgressStatus(null);
    gameStorage = new File("gameStorage/");
    if (!gameStorage.exists()) {
      gameJar.mkdirs();
    }

    gameJar = new File(gameStorage, "DroidTowers.jar");

    makeRequestForGameUpdates();
  }

  private void makeRequestForGameUpdates() {
    updateProgressStatus("Checking for updates...");

    GameUpdateCheckWorker updateCheckerWorker = new GameUpdateCheckWorker(gameStorage, gameJar);
    updateCheckerWorker.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String propertyName = propertyChangeEvent.getPropertyName();
        System.out.println("propertyName = " + propertyName);
        if (propertyName.equals("updateCheckComplete")) {
          if (((Boolean) propertyChangeEvent.getNewValue())) {
            updateProgressStatus("There is an update available, it will be automatically downloaded for you.");
          } else {
            updateProgressStatus("No updates found.");
          }
        } else if (propertyName.equals("updateProcessComplete")) {
          updateProgressStatus("Update complete!");
          startBuildingButton.setEnabled(true);
        }
      }
    });

    updateCheckerWorker.execute();

    new Thread() {
      @Override
      public void run() {
        gameUpdates.setEditable(false);
        try {
          gameUpdates.setPage(HappyDroidConsts.HAPPYDROIDS_URI + "/game-updates");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  private void launchGameFromJar(File mergedJarFile) {
    try {
      ClassLoaderUtil.addUrlToClassPath(mergedJarFile.toURI().toURL());

      Class aClass = ClassLoaderUtil.loadClass("com.happydroids.droidtowers.unhappyrobot.DesktopGame");
      Method main = aClass.getDeclaredMethod("main", String[].class);
      Object instance = aClass.newInstance();
      main.invoke(instance, new Object[]{null});

      dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateProgressStatus(String message) {
    if (message != null) {
      updateStatus.setForeground(Color.WHITE);
      updateStatus.setText(message);
      updateProgress.setEnabled(true);
    } else {
      updateStatus.setForeground(Color.darkGray);
      updateProgress.setEnabled(false);
    }
  }

  private void onCancel(WindowEvent e) {
    if (updateInProgress) {
      JOptionPane.showMessageDialog(null, "You are in the middle of updating core files, exiting now will be very bad.", "Wait!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    System.exit(0);
    dispose();
  }

  public static void main(final String[] args) {
    if (HappyDroidConsts.DEBUG) {
      Logger.getAnonymousLogger().setLevel(Level.FINEST);
    }

    BackgroundTask.setUncaughtExceptionHandler(new HappyDroidsDesktopUncaughtExceptionHandler());
    Thread.currentThread().setUncaughtExceptionHandler(new HappyDroidsDesktopUncaughtExceptionHandler());

    PlatformProtocolHandlerFactory.newInstance().initialize(args);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setDefaultLookAndFeelDecorated(true);

        SparkyMain window = new SparkyMain();
        window.addWindowStateListener(new WindowStateListener() {
          public void windowStateChanged(WindowEvent windowEvent) {
            System.out.println("windowEvent = " + windowEvent);
          }
        });
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
      }
    });
  }

  private class StartBuildingButtonClick implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      launchGameFromJar(gameJar);
    }
  }
}
