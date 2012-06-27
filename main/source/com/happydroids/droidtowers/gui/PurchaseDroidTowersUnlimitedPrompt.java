/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.happydroids.platform.Platform;

public class PurchaseDroidTowersUnlimitedPrompt extends Dialog {
  public PurchaseDroidTowersUnlimitedPrompt() {
    super();
    setTitle("Droid Towers: Unlimited");
    setMessage("Sorry but this feature is only available in Droid Towers: Unlimited.\n\nWould you like to unlock it?");

    addButton("Purchase Droid Towers: Unlimited", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
        Platform.getPurchaseManager().requestPurchaseForUnlimitedVersion();
      }
    });

    addButton(ResponseType.NEGATIVE, "Dismiss", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    });
  }
}
