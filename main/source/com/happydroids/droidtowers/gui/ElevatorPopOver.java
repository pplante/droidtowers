/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.ConfirmElevatorAddCarDialog;
import com.happydroids.droidtowers.gui.dialogs.ConfirmElevatorRemoveCarDialog;

import static com.happydroids.droidtowers.platform.Display.scale;

public class ElevatorPopOver extends GridObjectPopOver<Elevator> {
  private Label numRidersLabel;
  private Label numPassengersWaitingLabel;
  private Label numCarsLabel;


  public ElevatorPopOver(Elevator elevator) {
    super(elevator);
  }

  @Override
  protected void buildControls(final Elevator gridObject) {
    numCarsLabel = FontManager.Default.makeLabel("1");
    numRidersLabel = FontManager.Default.makeLabel("1");
    numPassengersWaitingLabel = FontManager.Default.makeLabel("1");

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Add Car", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ConfirmElevatorAddCarDialog(gridObject).show();
      }
    });

    buttonBar.addButton("Remove Car", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ConfirmElevatorRemoveCarDialog(gridObject).show();
      }
    });

    row();
    add(FontManager.Default.makeLabel("Cars in Service"));
    row();
    add(numCarsLabel);

    row();
    add(FontManager.Default.makeLabel("Current Riders"));
    row();
    add(numRidersLabel);

    row();
    add(FontManager.Default.makeLabel("Passengers Waiting"));
    row();
    add(numPassengersWaitingLabel);

    row().fillX().pad(scale(-8)).padTop(scale(16));
    add(buttonBar).expandX().minWidth(200);
  }

  @Override
  protected void updateControls() {
    numCarsLabel.setText("" + gridObject.getNumElevatorCars());
    numRidersLabel.setText("" + gridObject.getNumRiders());
    numPassengersWaitingLabel.setText("" + gridObject.getNumPassengersWaiting());
  }
}
