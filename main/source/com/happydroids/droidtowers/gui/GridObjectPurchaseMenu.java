/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PlacementTool;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

import static com.happydroids.droidtowers.platform.Display.scale;

public class GridObjectPurchaseMenu extends TowerWindow {
  private Class gridObjectTypeClass;
  private final Runnable toolCleanupRunnable;

  public GridObjectPurchaseMenu(Stage stage, Skin skin, String objectTypeName, GridObjectTypeFactory typeFactory, final Runnable toolCleanupRunnable) {
    super("Purchase " + objectTypeName, stage, skin);
    this.toolCleanupRunnable = toolCleanupRunnable;

    Table container = new Table();
    container.defaults();

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setFillParent(true);
    scrollPane.setWidget(container);
    add(scrollPane).fill();

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

          if (gridObjectType.getId().equalsIgnoreCase("GROUND-FLOOR-LOBBY")) {
            AchievementEngine.instance().completeAchievement("tutorial-purchased-lobby");
          }

          dismiss();
        }
      });

      container.row().fillX();
      container.add(purchaseItem).top().left().padBottom(scale(8)).padTop(scale(8)).expandX();
      container.row().fillX();
      container.add(new HorizontalRule(Color.DARK_GRAY, 2));
    }
  }
}
