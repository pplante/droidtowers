/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PickerTool;
import com.happydroids.droidtowers.types.*;

import static com.happydroids.droidtowers.platform.Display.scale;

public class ToolMenu extends RadialMenu {
  private static final String TAG = ToolMenu.class.getSimpleName();

  private GridObjectPurchaseMenu purchaseDialog;
  private final ImageButton housingButton;
  private final ImageButton transitButton;
  private final ImageButton commerceButton;
  private final ImageButton servicesButton;
  private final ImageButton sellButton;
  private final HudToolButton hudToolButton;


  public ToolMenu(TextureAtlas hudAtlas, HudToolButton hudToolButton) {
    super();
    this.hudToolButton = hudToolButton;

    arc = 35f;
    arcStart = -3.5f;
    radius = scale(180);
    rotation = 0;

    housingButton = new ImageButton(hudAtlas.findRegion("tool-housing"));
    transitButton = new ImageButton(hudAtlas.findRegion("tool-transit"));
    commerceButton = new ImageButton(hudAtlas.findRegion("tool-commerce"));
    servicesButton = new ImageButton(hudAtlas.findRegion("tool-services"));
    sellButton = new ImageButton(hudAtlas.findRegion("tool-sell"));

    addActor(housingButton);
    addActor(transitButton);
    addActor(commerceButton);
    addActor(servicesButton);
    addActor(sellButton);

    makeClickListeners();
  }

  private void makeClickListeners() {
    housingButton.setClickListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.instance()));
    transitButton.setClickListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.instance()));
    commerceButton.setClickListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.instance()));
    servicesButton.setClickListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.instance()));
    sellButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        close();

        hudToolButton.setStyle(sellButton.getStyle());

        InputSystem.instance().switchTool(GestureTool.SELL, new Runnable() {
          @Override
          public void run() {
            hudToolButton.resetStyle();
          }
        });
      }
    });
  }

  private ClickListener makePurchaseButtonClickListener(final String dialogTitle, final GridObjectTypeFactory typeFactory) {
    return new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        close();

        if (purchaseDialog == null) {
          if (typeFactory instanceof RoomTypeFactory) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
          }

          makePurchaseDialog(dialogTitle, typeFactory, ((ImageButton) actor).getStyle());
        } else {
          purchaseDialog.dismiss();
          purchaseDialog = null;
        }
      }
    };
  }

  private void makePurchaseDialog(String title, GridObjectTypeFactory typeFactory, final ImageButton.ImageButtonStyle purchaseButtonStyle) {
    purchaseDialog = new GridObjectPurchaseMenu(getStage(), title, typeFactory, new Runnable() {
      public void run() {
        hudToolButton.resetStyle();
      }
    });

    purchaseDialog.setDismissCallback(new Runnable() {
      public void run() {
        Gdx.app.log(TAG, "Tool: " + InputSystem.instance().getCurrentTool());
        purchaseDialog = null;
        if (InputSystem.instance().getCurrentTool() instanceof PickerTool) {
          hudToolButton.resetStyle();
        } else {
          hudToolButton.setStyle(purchaseButtonStyle);
        }
      }
    }).show();

  }
}
