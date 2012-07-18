/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.ProgressDialog;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tasks.VerifyPurchaseTask;

public class VerifyPurchaseScene extends SplashScene {
  private static final String TAG = VerifyPurchaseScene.class.getSimpleName();
  private boolean displayedDialog;
  private String serial;


  @Override
  public void create(Object... args) {
    super.create(args);

    if (args != null && args.length > 0) {
      serial = (String) args[0];
    } else {
      SceneManager.changeScene(MainMenuScene.class);
    }
  }

  @Override
  public void render(float deltaTime) {
    if (!displayedDialog && TowerAssetManager.preloadFinished()) {
      displayedDialog = true;
      ProgressDialog progressDialog = new ProgressDialog();
      progressDialog.hideButtons(true)
              .setMessage("Verifying Purchase")
              .setDismissCallback(new Runnable() {
                @Override
                public void run() {
                  if (SceneManager.previousScene() == null || SceneManager.previousScene() instanceof LaunchUriScene) {
                    SceneManager.changeScene(MainMenuScene.class);
                  } else {
                    SceneManager.popScene();
                  }
                }
              })
              .show();
      new VerifyPurchaseTask(serial, progressDialog).run();
    }
  }
}
