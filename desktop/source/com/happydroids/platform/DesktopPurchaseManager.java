/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.badlogic.gdx.Gdx;
import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.purchase.DroidTowerVersions;

public class DesktopPurchaseManager extends PlatformPurchaseManger {
  public DesktopPurchaseManager() {
    super();

    itemSkus.put(DroidTowerVersions.UNLIMITED_299, "com.happydroids.droidtowers.version.unlimited299");
  }

  @Override
  public void requestPurchase(final String itemId) {
    Gdx.app.postRunnable(new LaunchBrowserAfterDelay(HappyDroidConsts.HAPPYDROIDS_URI + "/commerce/purchase/overview?sku=" + itemId, 0.5f));
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }
}
