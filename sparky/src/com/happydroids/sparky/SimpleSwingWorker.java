/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public abstract class SimpleSwingWorker {

  private final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
    @Override
    protected Void doInBackground() throws Exception {
      SimpleSwingWorker.this.doInBackground();
      return null;
    }

    @Override
    protected void done() {
      // call get to make sure any exceptions
      // thrown during doInBackground() are
      // thrown again
      try {
        get();
      } catch (final Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  };

  public SimpleSwingWorker() {
  }

  protected abstract Void doInBackground() throws Exception;

  public void execute() {
    worker.execute();
  }

  protected void firePropertyChange(String eventName, Object oldValue, Object newValue) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    worker.firePropertyChange(eventName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    worker.addPropertyChangeListener(propertyChangeListener);
  }
}
