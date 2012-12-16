/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;
import com.happydroids.droidtowers.platform.Display;

public class CommercialSpacePopOver extends GridObjectPopOver<CommercialSpace> {
  private RatingBar crimeBar;
  private RatingBar employmentBar;
  private RatingBar dirtLevelBar;

  public CommercialSpacePopOver(final CommercialSpace commercialSpace) {
    super(commercialSpace);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    employmentBar = makeStarRatingBar("Employment");
    crimeBar = makeStarRatingBar("Crime");
    crimeBar.setTextures(RatingBar.SECURITY_ICON);
    dirtLevelBar = makeStarRatingBar("Dirt");
    dirtLevelBar.setTextures(RatingBar.COCKROACH_ICON);

    ButtonBar buttonBar = new ButtonBar();
    if (gridObject.hasLoanFromCousinVinnie()) {
      buttonBar.addButton("Repay Vinnie", new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          new CousinVinnieRepayLoanDialog(gridObject).show();
        }
      });
    }

    row().fillX().pad(Display.devicePixel(-8)).padTop(Display.devicePixel(16));
    add(buttonBar).expandX().minWidth(200);
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    crimeBar.setValue(gridObject.getSurroundingCrimeLevel() * 5f);
    employmentBar.setValue(gridObject.getEmploymentLevel() * 5f);
    dirtLevelBar.setValue(gridObject.getDirtLevel() * 5f);
  }
}
