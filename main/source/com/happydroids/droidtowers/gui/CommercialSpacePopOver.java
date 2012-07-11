/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;

import static com.happydroids.droidtowers.platform.Display.scale;

public class CommercialSpacePopOver extends GridObjectPopOver<CommercialSpace> {
  private StarRatingBar crimeBar;
  private StarRatingBar employmentBar;


  public CommercialSpacePopOver(final CommercialSpace commercialSpace) {
    super(commercialSpace);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    crimeBar = makeStarRatingBar("Crime");
    employmentBar = makeStarRatingBar("Employment");

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Manage", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ManageCommercialSpaceDialog(gridObject).show();
      }
    });

    if (gridObject.hasLoanFromCousinVinnie()) {
      buttonBar.addButton("Repay Vinnie", new VibrateClickListener() {
        @Override
        public void onClick(Actor actor, float x, float y) {
          new CousinVinnieRepayLoanDialog(gridObject).show();
        }
      });
    }

    row().fillX().pad(scale(-8)).padTop(scale(16));
    add(buttonBar).expandX().minWidth(200);
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    crimeBar.setValue(gridObject.getSurroundingCrimeLevel() * 5f);
    employmentBar.setValue(gridObject.getEmploymentLevel() * 5f);
  }
}
