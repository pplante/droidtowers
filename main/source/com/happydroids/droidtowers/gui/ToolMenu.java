/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PickerTool;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.types.*;

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
    radius = Display.devicePixel(180);
    setRotation(0);

    housingButton = new ColorizedImageButton(hudAtlas.findRegion("tool-housing"), Colors.ICS_BLUE);
    transitButton = new ColorizedImageButton(hudAtlas.findRegion("tool-transit"), Colors.ICS_BLUE);
    commerceButton = new ColorizedImageButton(hudAtlas.findRegion("tool-commerce"), Colors.ICS_BLUE);
    servicesButton = new ColorizedImageButton(hudAtlas.findRegion("tool-services"), Colors.ICS_BLUE);
    sellButton = new ColorizedImageButton(hudAtlas.findRegion("tool-sell"), Colors.ICS_BLUE);

    addActor(housingButton);
    addActor(transitButton);
    addActor(commerceButton);
    addActor(servicesButton);
    addActor(sellButton);

    makeClickListeners();
  }

  private void makeClickListeners() {
    housingButton.addListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.instance()));
    transitButton.addListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.instance()));
    commerceButton.addListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.instance()));
    servicesButton.addListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.instance()));
    sellButton.addListener(new VibrateClickListener() {
      public void onClick(InputEvent event, float x, float y) {
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
      public void onClick(InputEvent event, float x, float y) {
        close();

        if (purchaseDialog == null) {
          if (typeFactory instanceof RoomTypeFactory) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
          }

          makePurchaseDialog(dialogTitle, typeFactory, ((ImageButton) event.getListenerActor()).getStyle());
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
