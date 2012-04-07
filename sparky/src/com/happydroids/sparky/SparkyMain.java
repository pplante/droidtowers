/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.HappyDroidsDesktopUncaughtExceptionHandler;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.GameUpdate;
import com.happydroids.server.GameUpdateCollection;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.sparky.platform.PlatformProtocolHandlerFactory;
import com.happydroids.utils.BackgroundTask;
import jodd.util.ClassLoaderUtil;
import org.apache.http.HttpResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class SparkyMain extends JFrame {
  private JPanel contentPane;
  private JButton buttonOK;
  private JLabel happyDroidsLogo;
  private JProgressBar updateProgress;
  private JEditorPane gameUpdates;
  private JLabel updateStatus;
  private JPanel innerPane;
  private boolean updateInProgress;

  public SparkyMain() {
    setContentPane(contentPane);
    setSize(800, 480);
    setResizable(false);
    setTitle("Droid Towers");
    getRootPane().setDefaultButton(buttonOK);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

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
    makeRequestForGameUpdates();
  }

  private void makeRequestForGameUpdates() {
    new BackgroundTask() {
      @Override
      public void execute() {
        try {
          HappyDroidServiceCollection<GameUpdate> updates = new GameUpdateCollection();
          updates.fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<GameUpdate>>() {
            @Override
            public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<GameUpdate> collection) {
              System.out.println("statusCode = " + statusCode);
            }

            @Override
            public void onSuccess(HttpResponse response, HappyDroidServiceCollection<GameUpdate> collection) {
              for (GameUpdate gameUpdate : collection.getObjects()) {
                System.out.println("gameUpdate = " + gameUpdate);
              }
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.run();

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

  private void onOK() {
    final String gameVersion = "version:v0.10.50";
    final File mergedJarFile = new File("tmp/merged.jar");

    try {
      JarFile jarFile = new JarFile(mergedJarFile);
      if (jarFile != null) {
        System.out.println("Have jar!");
        if (jarFile.getManifest() != null) {
          Attributes mainAttributes = jarFile.getManifest().getMainAttributes();
          if (gameVersion.equalsIgnoreCase(mainAttributes.getValue("Game-Version"))) {
            launchGameFromJar(mergedJarFile);
            return;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    updateProgressStatus("Installing updates...");
    updateProgress.setValue(0);
    updateInProgress = true;

    try {

      final SwingWorker<Void, Void> jarMergeWorker = new GameJarMergeWorker(mergedJarFile, gameVersion);
      jarMergeWorker.addPropertyChangeListener(new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
          if (propertyChangeEvent.getPropertyName().equals("progress")) {
            updateProgress.setValue(jarMergeWorker.getProgress());
          } else if (propertyChangeEvent.getPropertyName().equals("done")) {
            if (jarMergeWorker.isCancelled()) {
              updateProgressStatus("Failed to update game files!");
            } else {
              updateInProgress = false;

              launchGameFromJar(mergedJarFile);
            }
          }
        }
      });

      jarMergeWorker.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    BackgroundTask.setUncaughtExceptionHandler(new HappyDroidsDesktopUncaughtExceptionHandler());
    Thread.setDefaultUncaughtExceptionHandler(new HappyDroidsDesktopUncaughtExceptionHandler());

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
}
