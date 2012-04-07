/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky.platform;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenURIHandler;

import javax.swing.*;

public class MacProtocolHandler implements PlatformProtocolHandler {
  public void initialize(String[] applicationArgs) {
    Application.getApplication().setOpenURIHandler(new OpenURIHandler() {
      public void openURI(AppEvent.OpenURIEvent openURIEvent) {
        JOptionPane.showMessageDialog(null, openURIEvent.getURI(), "URI!!", JOptionPane.INFORMATION_MESSAGE);
      }
    });
  }
}
