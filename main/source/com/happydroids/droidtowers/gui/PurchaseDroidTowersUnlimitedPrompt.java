/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.types.RoomTypeFactory;
import com.happydroids.platform.Platform;
import com.happydroids.security.SecurePreferences;

public class PurchaseDroidTowersUnlimitedPrompt extends ScrollableTowerWindow {

  public PurchaseDroidTowersUnlimitedPrompt() {
    super("Purchase Droid Towers: Unlimited", DroidTowersGame.getRootUiStage());

    TowerAssetManager.assetManager().finishLoading();

    defaults().top().left().pad(Display.devicePixel(4));

    addLabel("All these features will be enabled:", FontManager.RobotoBold18).expandX().padTop(Display.devicePixel(8));
    int indentPixels = Display.devicePixel(64);
    addLabel("* Towers taller up to 250 floors", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Unlock the Sky Lobby:", FontManager.RobotoBold18).padLeft(indentPixels);
    row();
    add(new Image(RoomTypeFactory.findTypeById("SKY-LOBBY").getTextureRegion(0))).center();
    addLabel("* Unlock Land expansions", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Earn income 50% faster", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Feeds the developers for less than $0.01 USD per day!", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* FREE content updates!", FontManager.Roboto24, Color.CYAN).padLeft(indentPixels);

    shoveContentUp();

    TextButton purchaseButton = FontManager.Roboto32.makeTextButton("Purchase for: $0.99", Color.GREEN);
    purchaseButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
        Platform.getPurchaseManager().requestPurchaseForUnlimitedVersion();
      }
    });

    TextButton dismissButton = FontManager.Roboto18.makeTextButton("No Thanks");
    dismissButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
//        determineIfSpecialOfferIsAvailable();
        dismiss();
      }
    });

    Table buttons = new Table();
    buttons.row().pad(Display.devicePixel(12)).fill();
    buttons.add(purchaseButton).expandX();
    buttons.add(dismissButton);
    setStaticFooter(buttons);
  }

  private void determineIfSpecialOfferIsAvailable() {
    SecurePreferences preferences = TowerGameService.instance().getPreferences();

    if (!preferences.contains("SHOWN_ONE_TIME_OFFER")) {
      preferences.putBoolean("SHOWN_ONE_TIME_OFFER", true);

      displaySpecialOfferDialog();
    }
  }

  private void displaySpecialOfferDialog() {
    new Dialog()
        .setTitle("One Time Offer!")
        .setMessage("SPECIAL ONE TIME OFFER!\n\nIf you are unsure about purchasing Droid Towers,\nhow about a special discount to sweeten the deal?\n\n* YOU WILL NEVER SEE THIS OFFER AGAIN *")
        .addButton("Purchase for $1.99", new OnClickCallback() {
          @Override public void onClick(Dialog dialog) {
            dialog.dismiss();
            Platform.getPurchaseManager().requestPurchaseForDiscountedOffer();
          }
        })
        .addButton("Never ask again", new OnClickCallback() {
          @Override public void onClick(Dialog dialog) {
            dialog.dismiss();
          }
        })
        .show();
  }

}
