/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform.purchase;

import com.happydroids.platform.PlatformPurchaseManger;
import com.happydroids.platform.purchase.DroidTowerVersions;
import netscape.javascript.JSObject;

public class AppletPurchaseManager extends PlatformPurchaseManger {
  private JSObject javascriptInterface;

  public AppletPurchaseManager() {
    super();
    itemSkus.put(DroidTowerVersions.UNLIMITED_299, "com.happydroids.droidtowers.versions.unlimited299");
  }

  @Override
  public void requestPurchase(String itemId) {
    if (javascriptInterface != null) {
      javascriptInterface.call("requestPurchase", new String[]{itemId});
    }
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }

  public void setJavascriptInterface(JSObject javascriptInterface) {
    this.javascriptInterface = javascriptInterface;
  }
}
