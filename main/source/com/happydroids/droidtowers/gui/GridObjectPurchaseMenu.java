/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PlacementTool;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

import java.text.NumberFormat;

public class GridObjectPurchaseMenu extends TowerWindow {
  private Class gridObjectTypeClass;
  private final Runnable toolCleanupRunnable;

  public GridObjectPurchaseMenu(Stage stage, Skin skin, String objectTypeName, GridObjectTypeFactory typeFactory, Runnable toolCleanupRunnable) {
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
      GridObjectType casted = typeFactory.castToObjectType(o);

      container.row();
      container.add(new GridObjectPurchaseItem(casted)).align(Align.LEFT | Align.TOP).padBottom(4).fill();
    }

    TextButton closeButton = new TextButton("Close", skin);
    closeButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    row().align(Align.RIGHT);
    add(closeButton).align(Align.RIGHT).pad(5);

    container.pack();
    scrollPane.pack();
    pack();
  }

  private class GridObjectPurchaseItem extends Table {
    public GridObjectPurchaseItem(final GridObjectType gridObjectType) {
      TextButton buyButton = new TextButton("Buy", skin);

      if (gridObjectType.isLocked()) {
        buyButton.setText("LOCKED");
      } else {
        buyButton.setClickListener(new ClickListener() {
          public void click(Actor actor, float x, float y) {
            InputSystem.instance().switchTool(GestureTool.PLACEMENT, toolCleanupRunnable);

            PlacementTool placementTool = (PlacementTool) InputSystem.instance().getCurrentTool();
            placementTool.setup(gridObjectType);
            placementTool.enterPurchaseMode();

            GridObjectPurchaseMenu.this.dismiss();
          }
        });
      }

      defaults().align(Align.LEFT | Align.TOP).pad(2).expand();

      row().fill();
      add(new Label(gridObjectType.getName(), skin)).minWidth(350);
      Label priceLabel = new Label(TowerConsts.CURRENCY_SYMBOL + NumberFormat.getInstance().format(gridObjectType.getCoins()), skin);
      priceLabel.setAlignment(Align.RIGHT);
      add(priceLabel).right().fill();

      row().align(Align.LEFT);
      TextureRegion textureRegion = gridObjectType.getTextureRegion();
      Actor actor;
      if (textureRegion != null) {
        actor = new Image(textureRegion, Scaling.fit, Align.LEFT | Align.TOP);
      } else {
        actor = new Label("No image found.", skin);
      }
      add(actor).maxHeight(40).maxWidth(200);
      add(buyButton).align(Align.RIGHT).width(80);
    }
  }
}
