/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Transit;
import com.happydroids.droidtowers.graphics.effects.SmokeParticleEffect;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.utils.StringUtils;

import java.text.NumberFormat;

public class SellGridObjectConfirmationDialog extends Dialog {
  public SellGridObjectConfirmationDialog(final GameGrid gameGrid, final GridObject objectToSell) {
    super();

    final int sellPrice = (int) (objectToSell.getGridObjectType().getCoins() * 0.5);

    setTitle("Are you sure?");

    if (objectToSell.getAmountLoanedFromCousinVinnie() > 0) {
      setMessage("You must pay back Cousin Vinnie before you can sell his hideout in: " + objectToSell.getName() + "\n\nDo you want to pay him back: " + TowerConsts.CURRENCY_SYMBOL + StringUtils
                                                                                                                                                                                               .formatNumber(objectToSell
                                                                                                                                                                                                                     .getAmountLoanedFromCousinVinnie()) + "?");
      addButton("Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          if (Player.instance().getCoins() < objectToSell.getAmountLoanedFromCousinVinnie()) {
            new Dialog()
                    .setMessage("Sorry you do not have enough money right now.")
                    .show();
          } else {
            Player.instance().subtractCurrency(objectToSell.getAmountLoanedFromCousinVinnie());
            sellGridObject(gameGrid, objectToSell, sellPrice);
          }
        }
      });
    } else {
      String message = "Are you sure you want to recycle this " + objectToSell.getName() + "?" +
                               "\n\nRecycled materials price is: $" + NumberFormat.getInstance().format(sellPrice);

      if (!(objectToSell instanceof Transit)) {
        message += "\n\nThis will also fire Employees, or evict Residents.";
      }

      setMessage(message);
      addButton("Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          sellGridObject(gameGrid, objectToSell, sellPrice);
        }
      });
    }


    addButton("No", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    });
  }

  private void sellGridObject(GameGrid gameGrid, GridObject objectToSell, int sellPrice) {
    dismiss();
    Gdx.input.vibrate(100);

    gameGrid.removeObject(objectToSell);
    Player.instance().addCurrency(sellPrice);

    SmokeParticleEffect smokeParticleEffect = new SmokeParticleEffect();
    smokeParticleEffect.setPosition(objectToSell.getWorldCenter());
    smokeParticleEffect.setSize(objectToSell.getWorldBounds().width, objectToSell.getWorldBounds().height);
    smokeParticleEffect.start();
    gameGrid.addChild(smokeParticleEffect);
  }
}
