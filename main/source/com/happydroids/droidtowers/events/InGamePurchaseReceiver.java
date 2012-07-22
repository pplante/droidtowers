/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.PurchaseAppreciationDialog;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.platform.purchase.RefundEvent;

public class InGamePurchaseReceiver {
  @Subscribe
  public void PurchaseManager_onPurchase(final PurchaseEvent event) {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (TowerAssetManager.preloadFinished()) {
          new PurchaseAppreciationDialog().show();
        } else {
          Gdx.app.postRunnable(this);
        }
      }
    });
//
//    new BackgroundTask() {
//      @Override
//      protected void execute() throws Exception {
//        new Payment(event.itemId(), event.orderId(), event.sourceMarket()).save();
//      }
//    }.run();

    if (SceneManager.activeScene() instanceof MainMenuScene) {
      SceneManager.restartActiveScene();
    }
  }

  @Subscribe
  public void PurchaseManager_onRefund(RefundEvent event) {
    if (SceneManager.activeScene() instanceof MainMenuScene) {
      SceneManager.restartActiveScene();
    }
  }
}
