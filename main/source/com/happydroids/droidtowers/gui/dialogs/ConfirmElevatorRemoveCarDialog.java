/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.dialogs;

import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;

public class ConfirmElevatorRemoveCarDialog extends Dialog {
  public ConfirmElevatorRemoveCarDialog(final Elevator gridObject) {
    super();

    if (gridObject.getNumElevatorCars() > 0) {
      setMessage("Removing an elevator car will reduce the efficiency of your transit system.\n\nDo you want to continue?");
      addButton("Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
          gridObject.removeCar();
        }
      });
      addButton("No", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
        }
      });
    } else {
      setMessage("Sorry there are no active elevator cars.");
    }
  }
}
