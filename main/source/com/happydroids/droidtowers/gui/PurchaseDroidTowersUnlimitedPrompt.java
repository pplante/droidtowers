/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.types.RoomTypeFactory;
import com.happydroids.platform.Platform;

import static com.happydroids.droidtowers.platform.Display.scale;

public class PurchaseDroidTowersUnlimitedPrompt extends ScrollableTowerWindow {

  public PurchaseDroidTowersUnlimitedPrompt() {
    super("Purchase Droid Towers: Unlimited", TowerGame.getRootUiStage());

    TowerAssetManager.assetManager().finishLoading();

    defaults().top().left().pad(scale(4));

    addLabel("All these features will be enabled:", FontManager.RobotoBold18).expandX().padTop(scale(8));
    int indentPixels = scale(64);
    addLabel("* Towers taller up to 250 floors", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Unlock the Sky Lobby:", FontManager.RobotoBold18).padLeft(indentPixels);
    row();
    add(new Image(RoomTypeFactory.findTypeById("SKY-LOBBY").getTextureRegion())).center();
    addLabel("* Unlock Land expansions", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Earn income 50% faster", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* Feeds the developers for less than $0.01 USD per day!", FontManager.RobotoBold18).padLeft(indentPixels);
    addLabel("* FREE monthly content updates!", FontManager.Roboto24, Color.CYAN).padLeft(indentPixels);

    shoveContentUp();

    TextButton purchaseButton = FontManager.Roboto32.makeTextButton("Purchase for: $2.99", Color.GREEN);
    purchaseButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        dismiss();
        Platform.getPurchaseManager().requestPurchaseForUnlimitedVersion();
      }
    });

    TextButton dismissButton = FontManager.Roboto18.makeTextButton("No Thanks");
    dismissButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        dismiss();
      }
    });



    Table buttons = new Table();
    buttons.row().pad(scale(12)).fill();
    buttons.add(purchaseButton).expandX();
    buttons.add(dismissButton);
    setStaticFooter(buttons);
  }

  private Cell addLabel(String text, FontManager labelFont) {
    return addLabel(text, labelFont, Color.WHITE);
  }

  private Cell addLabel(String text, FontManager labelFont, Color color) {
    row();
    return add(labelFont.makeLabel(text, color)).colspan(2);
  }
}
