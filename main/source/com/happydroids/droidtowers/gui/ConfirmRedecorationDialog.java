/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;

public class ConfirmRedecorationDialog extends Dialog {
  public ConfirmRedecorationDialog(final GridObject gridObject) {
    super();

    setMessage("Would you like to redecorate this room?\n\nIt will cost $1,000.");
    addButton("Yes", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();

        int variationId = gridObject.getVariationId() + 1;
        if (variationId > gridObject.getGridObjectType().getNumVariations()) {
          variationId = 1;
        }

        gridObject.setVariationId(variationId);
        gridObject.updateSprite();

        Player.instance().subtractCurrency(1000);
      }
    });

    addButton("No", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });
  }
}
