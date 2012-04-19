/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.types.GridObjectType;

import java.text.NumberFormat;

class GridObjectPurchaseItem extends Table {
  private static NinePatch background;
  private static Pixmap pixmap;
  private final TextButton buyButton;
  private final GridObjectType gridObjectType;

  public GridObjectPurchaseItem(final GridObjectType gridObjectType, Skin skin) {
    this.gridObjectType = gridObjectType;

    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGB888);
      pixmap.setColor(Color.BLACK);
      pixmap.fill();
      pixmap.setColor(new Color(0.075f, 0.075f, 0.075f, 1f));
      pixmap.drawPixel(0, 0);
      pixmap.drawPixel(1, 0);

      Texture texture = new Texture(pixmap);
      texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
      texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
      background = new NinePatch(texture);
    }

    buyButton = new TextButton(gridObjectType.isLocked() ? "LOCKED" : "Buy", skin);

    defaults().align(Align.LEFT | Align.TOP).pad(2).expand();

    row().fill();
    add(new Label(gridObjectType.getName(), skin)).minWidth(350);
    Label priceLabel = FontManager.Default.makeLabel(TowerConsts.CURRENCY_SYMBOL + NumberFormat.getInstance().format(gridObjectType.getCoins()));
    priceLabel.setAlignment(Align.RIGHT);
    add(priceLabel).right().fill();

    row().align(Align.LEFT);
    TextureRegion textureRegion = gridObjectType.getTextureRegion();
    Actor actor;
    if (textureRegion != null) {
      actor = new Image(textureRegion, Scaling.fit, Align.LEFT | Align.TOP);
    } else {
      actor = FontManager.Default.makeLabel("No image found.");
    }
    add(actor).maxHeight(40).maxWidth(200);
    add(buyButton).align(Align.RIGHT).width(80);
  }

  public void setBuyClickListener(ClickListener clickListener) {
    if (!gridObjectType.isLocked()) {
      buyButton.setClickListener(clickListener);
    }
  }
}
