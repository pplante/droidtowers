/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.utils.StringUtils;

public class CousinVinnieRepayLoanDialog extends Dialog {
  public CousinVinnieRepayLoanDialog(final Room gridObject) {
    super();

    TextureRegionDrawable cousinVinnieTexture = TowerAssetManager.drawableFromAtlas("droid-cousin-vinnie", "hud/menus.txt");
    Image cousinVinnieImage = new Image(cousinVinnieTexture, Scaling.none);

    final Table c = new Table();
    c.pad(Display.devicePixel(4));

    c.row();
    String loanAmount = TowerConsts.CURRENCY_SYMBOL + StringUtils.formatNumber(gridObject.getAmountLoanedFromCousinVinnie());
    final float repayAmount = gridObject.getAmountLoanedFromCousinVinnie() * 1.25f;
    String repayAmountStr = TowerConsts.CURRENCY_SYMBOL + StringUtils.formatNumber(repayAmount);
    c.add(FontManager.Roboto18.makeLabel("Hey Cousin,\n" +
                                                 "\n" +
                                                 "So you want to pay me off eh?.\n" +
                                                 "\n" +
                                                 "I loaned you " + loanAmount + " at 25% interest,\n" +
                                                 " so I think " + repayAmountStr + " sounds about right.\n" +
                                                 "\n" +
                                                 "Regards,\n" +
                                                 "Vinnie "));
    c.add(cousinVinnieImage).spaceLeft(Display.devicePixel(20)).top().width(Display.devicePixel(96));

    addButton("Pay " + repayAmountStr, new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        if (Player.instance().getCoins() < repayAmount) {
          new Dialog()
                  .setMessage("You do not have enough money to pay Vinnie back.")
                  .show();
        } else {
          Player.instance().subtractCurrency((long) repayAmount);
          gridObject.removeLoanFromVinnie();
        }
        dismiss();
      }
    });

    addButton("Maybe later", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });

    setView(c);
  }
}
