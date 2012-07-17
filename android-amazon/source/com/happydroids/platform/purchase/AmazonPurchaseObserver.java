/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import android.content.Context;
import com.amazon.inapp.purchasing.*;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformPurchaseManger;

public class AmazonPurchaseObserver extends PurchasingObserver {
  public AmazonPurchaseObserver(Context context) {
    super(context);
  }

  @Override
  public void onSdkAvailable(final boolean isSandboxMode) {
    PurchasingManager.initiateGetUserIdRequest();
  }

  @Override
  public void onGetUserIdResponse(GetUserIdResponse getUserIdResponse) {

  }

  @Override
  public void onPurchaseResponse(PurchaseResponse purchaseResponse) {
    PlatformPurchaseManger platformPurchaseManger = Platform.getPurchaseManager();
    String itemSku = platformPurchaseManger.getPurchases().getString(purchaseResponse.getRequestId());

    switch (purchaseResponse.getPurchaseRequestStatus()) {
      case ALREADY_ENTITLED:
      case SUCCESSFUL:
        Receipt receipt = purchaseResponse.getReceipt();
        String purchaseToken = receipt != null ? receipt.getPurchaseToken() : "UNKNOWN_AMAZON?";
        platformPurchaseManger.purchaseItem(itemSku, purchaseToken);
        break;
      case FAILED:
      case INVALID_SKU:
        platformPurchaseManger.revokeItem(itemSku);
        break;
    }
  }

  @Override
  public void onItemDataResponse(ItemDataResponse itemDataResponse) {
  }

  @Override
  public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse) {
  }
}
