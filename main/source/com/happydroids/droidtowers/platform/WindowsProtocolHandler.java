/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import javax.swing.*;
import java.net.URI;

public class WindowsProtocolHandler implements PlatformProtocolHandler {
  public void initialize(String[] applicationArgs) {
    JOptionPane.showMessageDialog(null, applicationArgs, "URI!!", JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean hasUri() {
    return false;
  }

  public URI consumeUri() {
    return null;
  }

  public void setUrl(URI uri) {

  }
}
