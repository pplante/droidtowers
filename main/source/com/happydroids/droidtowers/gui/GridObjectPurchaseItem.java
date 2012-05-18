/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.types.GridObjectType;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

class GridObjectPurchaseItem extends Table {
  private final TextButton buyButton;
  private final GridObjectType gridObjectType;

  public GridObjectPurchaseItem(final GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;

    buyButton = FontManager.RobotoBold18.makeTextButton(gridObjectType.isLocked() ? "LOCKED" : "Buy");

    defaults().align(Align.LEFT | Align.TOP).pad(scale(2)).expand();

    row().expandX();
    add(FontManager.RobotoBold18.makeLabel(gridObjectType.getName())).minWidth(350).fillX();
    Label priceLabel = FontManager.RobotoBold18.makeLabel(TowerConsts.CURRENCY_SYMBOL + NumberFormat.getInstance().format(gridObjectType.getCoins()));
    priceLabel.setAlignment(Align.RIGHT);
    add(priceLabel).right().fill().padBottom(scale(8));

    row().align(Align.LEFT);
    TextureRegion textureRegion = gridObjectType.getTextureRegion();
    Actor actor;
    if (textureRegion != null) {
      actor = new Image(textureRegion, Scaling.fit, Align.LEFT | Align.TOP);
    } else {
      actor = FontManager.Default.makeLabel("No image found.");
    }
    add(actor).maxHeight(scale(40)).maxWidth(scale(200));
    add(buyButton).align(Align.RIGHT).width(scale(80));
  }

  public void setBuyClickListener(ClickListener clickListener) {
    if (!gridObjectType.isLocked()) {
      buyButton.setClickListener(clickListener);
    }
  }
}
