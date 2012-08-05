/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

public class GridObjectPurchaseMenu extends ScrollableTowerWindow {
  private Class gridObjectTypeClass;

  public GridObjectPurchaseMenu(Stage stage, String objectTypeName, GridObjectTypeFactory typeFactory, final Runnable toolCleanupRunnable) {
    super("Purchase " + objectTypeName, stage);

    float biggestWidth = 0;
    for (Object o : typeFactory.all()) {
      final GridObjectType gridObjectType = typeFactory.castToObjectType(o);

      GridObjectPurchaseItem purchaseItem = new GridObjectPurchaseItem(gridObjectType);
      purchaseItem.setBuyClickListener(new SelectGridItemForPurchaseClickListener(this, toolCleanupRunnable, gridObjectType));

      row().fillX();
      add(purchaseItem).top().left().padBottom(Display.devicePixel(8)).padTop(Display.devicePixel(8)).expandX();
      row().fillX();
      add(new HorizontalRule(Color.DARK_GRAY, 2));
    }

    shoveContentUp();
  }

}
