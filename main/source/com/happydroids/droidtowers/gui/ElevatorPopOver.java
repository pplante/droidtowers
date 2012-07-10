/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.entities.Elevator;

public class ElevatorPopOver extends GridObjectPopOver<Elevator> {
  private Label numRidersLabel;
  private Label numPassengersWaitingLabel;


  public ElevatorPopOver(Elevator elevator) {
    super(elevator);
  }

  @Override
  protected void buildControls(Elevator gridObject) {
    numRidersLabel = FontManager.Default.makeLabel("1");
    numPassengersWaitingLabel = FontManager.Default.makeLabel("1");

    row();
    add(FontManager.Default.makeLabel("Current Riders"));
    row();
    add(numRidersLabel);

    row();
    add(FontManager.Default.makeLabel("Passengers Waiting"));
    row();
    add(numPassengersWaitingLabel);
  }

  @Override
  protected void updateControls() {
    numRidersLabel.setText("" + gridObject.getNumRiders());
    numPassengersWaitingLabel.setText("" + gridObject.getNumPassengersWaiting());
  }
}
