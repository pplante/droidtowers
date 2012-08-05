/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.ConfirmElevatorAddCarDialog;
import com.happydroids.droidtowers.gui.dialogs.ConfirmElevatorRemoveCarDialog;
import com.happydroids.droidtowers.platform.Display;

public class ElevatorPopOver extends GridObjectPopOver<Elevator> {
  private Label numRidersLabel;
  private Label numPassengersWaitingLabel;
  private Label numCarsLabel;


  public ElevatorPopOver(Elevator elevator) {
    super(elevator);
  }

  @Override
  protected void buildControls() {
    numCarsLabel = FontManager.RobotoBold18.makeLabel("1");
    numRidersLabel = FontManager.RobotoBold18.makeLabel("1");
    numPassengersWaitingLabel = FontManager.RobotoBold18.makeLabel("1");

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Add Car", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new ConfirmElevatorAddCarDialog(gridObject).show();
      }
    });

    buttonBar.addButton("Remove Car", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new ConfirmElevatorRemoveCarDialog(gridObject).show();
      }
    });

    row();
    add(FontManager.Roboto12.makeLabel("NUM. CARS"));
    row();
    add(numCarsLabel);

    row();
    add(FontManager.Roboto12.makeLabel("NUM. RIDERS"));
    row();
    add(numRidersLabel);

    row();
    add(FontManager.Roboto12.makeLabel("NUM. WAITING"));
    row();
    add(numPassengersWaitingLabel);

    row().fillX().pad(Display.devicePixel(-8)).padTop(Display.devicePixel(16));
    add(buttonBar).expandX().minWidth(200);

    pack();
  }

  @Override
  protected void updateControls() {
    numCarsLabel.setText("" + gridObject.getNumElevatorCars());
    numRidersLabel.setText("" + gridObject.getNumRiders());
    numPassengersWaitingLabel.setText("" + gridObject.getNumPassengersWaiting());
  }
}
