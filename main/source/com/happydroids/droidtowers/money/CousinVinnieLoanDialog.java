/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.money;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.utils.StringUtils;

public class CousinVinnieLoanDialog extends Dialog {
  public CousinVinnieLoanDialog(final GameGrid gameGrid) {
    super();

    final int amountToLoan = MathUtils.random(5000, 100000);

    TextureRegionDrawable cousinVinnieTexture = TowerAssetManager.drawableFromAtlas("droid-cousin-vinnie", "hud/menus.txt");
    Image cousinVinnieImage = new Image(cousinVinnieTexture, Scaling.none);

    Table c = new Table();
    c.pad(Display.devicePixel(4));

    c.row();
    c.add(FontManager.Roboto18
                  .makeLabel("Hey Cousin,\n\nLooks like you're out of money.\n\nI would be willing to loan you " + TowerConsts.CURRENCY_SYMBOL + StringUtils
                                                                                                                                                         .formatNumber(amountToLoan) + ",\nin return I need somewhere to setup a hideout.\n\nI promise you wont regret it.\n\nRegards,\nVinnie "));
    c.add(cousinVinnieImage).spaceLeft(Display.devicePixel(20)).top().width(Display.devicePixel(96));


    addButton("Accept", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();

        new CousinVinnieAcceptedLoanDialog(gameGrid, amountToLoan).show();
      }
    });

    addButton("No thanks!", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });

    setView(c);
  }
}
