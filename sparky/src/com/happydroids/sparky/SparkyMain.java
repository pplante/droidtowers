/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.Platform;
import com.happydroids.server.HappyDroidService;
import com.happydroids.utils.BackgroundTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SparkyMain extends JFrame {
  private JPanel contentPane;
  private JLabel happyDroidsLogo;
  private JProgressBar updateProgress;
  private JLabel updateStatus;
  private JPanel innerPane;
  private JButton startBuildingButton;
  private JScrollPane scrollPane;
  private JEditorPane webPane;
  private JPanel bottomPanel;
  private BackgroundPanel backgroundPanel;
  private boolean updateInProgress;
  private File gameStorage;
  private File gameJar;

  public SparkyMain() {
    setContentPane(contentPane);
    setSize(contentPane.getWidth(), contentPane.getHeight());
    setResizable(false);
    setTitle("Droid Towers");
    getRootPane().setDefaultButton(startBuildingButton);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    scrollPane.setBorder(null);
    contentPane.setBorder(null);
    bottomPanel.setBorder(null);
    bottomPanel.setOpaque(false);
    scrollPane.setBackground(new Color(0, 0, 0, 0));
    scrollPane.setOpaque(false);

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
    gameStorage = Platform.getAppRoot();

    gameJar = new File(gameStorage, "DroidTowers.jar");

    makeRequestForGameUpdates();
  }

  private void createUIComponents() {
    backgroundPanel = new BackgroundPanel(new ImageIcon("assets/background.png").getImage(), BackgroundPanel.TILED, 1f, 0f);
    backgroundPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    backgroundPanel.setTransparentAdd(true);
  }

  private void makeRequestForGameUpdates() {
    updateInProgress = true;
    updateProgress.setMaximum(100);

    updateProgressStatus("Checking for updates...");
    HappyDroidService.instance().withNetworkConnection(new Runnable() {
      public void run() {
        GameUpdateCheckWorker updateCheckerWorker = new GameUpdateCheckWorker(gameStorage, gameJar);
        updateCheckerWorker.addPropertyChangeListener(new GameUpdateCheckListener());
        updateCheckerWorker.execute();


        new BackgroundTask() {
          @Override
          public void execute() {
            try {
              webPane.setPage(HappyDroidConsts.HAPPYDROIDS_URI + "/game-updates");
            } catch (IOException ignored) {

            }
          }
        }.execute();
      }
    });
  }

  private void launchGameFromJar(File mergedJarFile) {
    try {
      URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
      Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
      addURL.setAccessible(true);
      addURL.invoke(classLoader, mergedJarFile.toURI().toURL());

      Class aClass = classLoader.loadClass("com.happydroids.droidtowers.DesktopGame");
      Method main = aClass.getDeclaredMethod("main", String[].class);
      Object instance = aClass.newInstance();
      main.invoke(instance, new Object[]{null});

      dispose();
    } catch (Exception e) {
      throw new RuntimeException("Unable to start game.");
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

//    BackgroundTask.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
//    Thread.setDefaultUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());

//    PlatformProtocolHandlerFactory.newInstance().initialize(args);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setDefaultLookAndFeelDecorated(true);

        SparkyMain window = new SparkyMain();
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

  private class GameUpdateCheckListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
      String propertyName = propertyChangeEvent.getPropertyName();
      if (propertyName.equals("state")) {
        return;
      }

      try {
        Method declaredMethod = GameUpdateCheckListener.class.getDeclaredMethod(propertyName, new Class[]{PropertyChangeEvent.class});
        if (declaredMethod != null) {
          declaredMethod.invoke(this, propertyChangeEvent);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private void updateDownloadProgress(PropertyChangeEvent event) {
      updateProgressStatus("Downloading update...");
      updateProgressBarFromEvent(event);
    }

    private void updateProcessingProgress(PropertyChangeEvent event) {
      updateProgressStatus("Extracting update...");
      updateProgressBarFromEvent(event);
    }

    private void updateProcessComplete(PropertyChangeEvent event) {
      updateInProgress = false;

      if (gameJar.exists()) {
        updateProgress.setValue(100);
        updateProgressStatus("Update complete!");
        startBuildingButton.setEnabled(true);
      } else {
//            JOptionPane.showMessageDialog(null, "Sorry, there was a problem contacting the update server\nto download a copy of Droid Towers.\n\nPlease check your internet connection then try again.", "Connection Problem: ETF0NH0M3", JOptionPane.INFORMATION_MESSAGE);
        updateProgressStatus("Update failed!");
        updateProgress.setValue(0);
        updateProgress.setEnabled(false);
      }
    }

    private void updateCheckComplete(PropertyChangeEvent event) {
      if (((Boolean) event.getNewValue())) {
        updateProgressStatus("There is an update available, it will be automatically downloaded for you.");
      } else {
        updateProgressStatus("No updates found.");
      }
    }

    private void updateProgressBarFromEvent(PropertyChangeEvent event) {
      Integer totalBytesToDownload = (Integer) event.getOldValue();
      Integer totalBytesDownloaded = (Integer) event.getNewValue();
      double progress = ((float) totalBytesDownloaded / (float) totalBytesToDownload) * 100.0;

      updateProgress.setValue((int) progress);
    }
  }
}
