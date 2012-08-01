/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import javax.swing.*;

public class DesktopDialogOpener implements PlatformDialogOpener {
  @Override
  public void showAlert(String title, String message) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  }
}
