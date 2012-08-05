/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PlacementTool;
import com.happydroids.droidtowers.types.GridObjectType;

class SelectGridItemForPurchaseClickListener extends VibrateClickListener {
  private final Runnable toolCleanupRunnable;
  private final GridObjectType gridObjectType;
  private GridObjectPurchaseMenu gridObjectPurchaseMenu;

  public SelectGridItemForPurchaseClickListener(GridObjectPurchaseMenu gridObjectPurchaseMenu, Runnable toolCleanupRunnable, GridObjectType gridObjectType) {
    this.gridObjectPurchaseMenu = gridObjectPurchaseMenu;
    this.toolCleanupRunnable = toolCleanupRunnable;
    this.gridObjectType = gridObjectType;
  }

  @Override
  public void onClick(InputEvent event, float x, float y) {
    InputSystem.instance().switchTool(GestureTool.PLACEMENT, toolCleanupRunnable);

    PlacementTool placementTool = (PlacementTool) InputSystem.instance().getCurrentTool();
    placementTool.setup(gridObjectType);
    placementTool.enterPurchaseMode();

    if (gridObjectType.getId().equalsIgnoreCase("GROUND-FLOOR-LOBBY")) {
      TutorialEngine.instance().moveToStepWhenReady("tutorial-purchased-lobby");
    }

    gridObjectPurchaseMenu.dismiss();
  }
}
