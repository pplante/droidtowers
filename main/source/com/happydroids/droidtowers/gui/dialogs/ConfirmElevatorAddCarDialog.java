/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.dialogs;

import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.utils.StringUtils;

public class ConfirmElevatorAddCarDialog extends Dialog {

  public ConfirmElevatorAddCarDialog(final Elevator gridObject) {
    super();

    if (gridObject.canAddElevatorCar()) {
      final int costForCar = gridObject.getGridObjectType().getCoins() / 3;

      setMessage("Purchasing another elevator car will cost $" + StringUtils.formatNumber(costForCar) + " now, and\nwill increase the monthly upkeep of this elevator.\n\nDo you want to continue?");
      addButton("Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
          Player.instance().subtractCurrency(costForCar);
          gridObject.addCar();
        }
      });
      addButton("No", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
        }
      });
    } else {
      setMessage("Sorry this elevator cannot accept anymore cars.");
    }
  }
}
