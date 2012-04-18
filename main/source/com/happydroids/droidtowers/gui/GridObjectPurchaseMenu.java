/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PlacementTool;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

public class GridObjectPurchaseMenu extends TowerWindow {
  private Class gridObjectTypeClass;
  private final Runnable toolCleanupRunnable;

  public GridObjectPurchaseMenu(Stage stage, Skin skin, String objectTypeName, GridObjectTypeFactory typeFactory, final Runnable toolCleanupRunnable) {
    super("Purchase " + objectTypeName, stage, skin);
    this.toolCleanupRunnable = toolCleanupRunnable;

    defaults().align(Align.LEFT);
    row().pad(10);

    Table container = newTable();
    container.row();

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(container);
    add(scrollPane).maxWidth(500).maxHeight(300).minWidth(400);

    float biggestWidth = 0;
    for (Object o : typeFactory.all()) {
      final GridObjectType gridObjectType = typeFactory.castToObjectType(o);

      GridObjectPurchaseItem purchaseItem = new GridObjectPurchaseItem(gridObjectType, skin);
      purchaseItem.setBuyClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          InputSystem.instance().switchTool(GestureTool.PLACEMENT, toolCleanupRunnable);

          PlacementTool placementTool = (PlacementTool) InputSystem.instance().getCurrentTool();
          placementTool.setup(gridObjectType);
          placementTool.enterPurchaseMode();

          dismiss();
        }
      });

      container.row();
      container.add(purchaseItem).top().left().padBottom(4).fill();
    }

    TextButton closeButton = new TextButton("Close", skin);
    closeButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    row().right();
    add(closeButton).right().pad(5);

    container.pack();
    scrollPane.pack();
    pack();
  }
}
