/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.events.PurchaseEvent;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.platform.purchase.DroidTowerVersions;
import com.happydroids.platform.purchase.RefundEvent;
import com.happydroids.security.SecurePreferences;

import java.util.HashMap;
import java.util.Map;

import static com.happydroids.platform.purchase.DroidTowerVersions.UNLIMITED_299;

public abstract class PlatformPurchaseManger {
  private static Runnable initializeRunnable;
  protected final HashMap<DroidTowerVersions, String> itemSkus;
  protected final SecurePreferences purchases;
  private boolean purchasesEnabled;
  private EventBus eventBus;


  public PlatformPurchaseManger() {
    eventBus = new EventBus(PlatformPurchaseManger.class.getSimpleName());
    purchasesEnabled = false;
    purchases = TowerGameService.instance().getPreferences();
    if (initializeRunnable != null) {
      initializeRunnable.run();
    }

    itemSkus = Maps.newHashMap();
  }

  public static void setInitializeRunnable(Runnable initializeRunnable) {
    PlatformPurchaseManger.initializeRunnable = initializeRunnable;
  }

  public void purchaseItem(String itemId, String purchaseToken) {
    if (!purchases.contains(itemId)) {
      purchases.putString(itemId, purchaseToken);
      purchases.flush();

      eventBus.post(new PurchaseEvent(purchaseToken, itemId));
    }
  }

  public void revokeItem(String itemId) {
    if (purchases.contains(itemId)) {
      purchases.remove(itemId);
      purchases.flush();

      eventBus.post(new RefundEvent(itemId));
    }
  }

  public boolean hasPurchasedUnlimitedVersion() {
    return purchases.contains(getSkuForVersion(UNLIMITED_299));
  }

  public abstract void requestPurchase(String itemId);

  public void enablePurchases() {
    purchasesEnabled = true;
  }

  public void requestPurchaseForUnlimitedVersion() {
    requestPurchase(getSkuForVersion(UNLIMITED_299));
  }

  public SecurePreferences getPurchases() {
    return purchases;
  }

  public abstract void onStart();

  public abstract void onResume();

  private DroidTowerVersions getVersionForSku(String itemSku) {
    for (Map.Entry<DroidTowerVersions, String> entry : itemSkus.entrySet()) {
      if (entry.getValue().equals(itemSku)) {
        return entry.getKey();
      }
    }
    return null;
  }

  public String getSkuForVersion(DroidTowerVersions version) {
    return itemSkus.get(version);
  }

  public EventBus events() {
    return eventBus;
  }
}
