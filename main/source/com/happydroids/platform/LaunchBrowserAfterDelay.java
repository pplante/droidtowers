/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.actions.TimeDelayedAction;
import com.happydroids.droidtowers.gui.ProgressDialog;
import com.happydroids.droidtowers.gui.VibrateClickListener;

public class LaunchBrowserAfterDelay implements Runnable {
  private final String uri;
  private final float launchDelay;
  private TimeDelayedAction launchAction;
  private ProgressDialog progressDialog;

  public LaunchBrowserAfterDelay(final String uri, final float launchDelay) {
    this.uri = uri;
    this.launchDelay = launchDelay;

    launchAction = new TimeDelayedAction(launchDelay) {
      @Override
      public void run() {
        markToRemove();
        progressDialog.dismiss();
      }
    };

    progressDialog = new ProgressDialog();
    progressDialog.setMessage("Opening Browser")
            .addButton("Launch Now", new VibrateClickListener() {
              @Override
              public void onClick(InputEvent event, float x, float y) {
                progressDialog.dismiss();
                ActionManager.instance().removeAction(launchAction);
              }
            })
            .setDismissCallback(new Runnable() {
              @Override
              public void run() {
                Platform.getBrowserUtil().launchWebBrowser(uri);
              }
            });
  }

  @Override
  public void run() {
    progressDialog.show();

    ActionManager.instance().addAction(launchAction);
  }
}
