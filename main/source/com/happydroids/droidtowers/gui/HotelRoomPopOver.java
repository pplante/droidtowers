/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.entities.HotelRoom;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;
import com.happydroids.droidtowers.platform.Display;

public class HotelRoomPopOver extends GridObjectPopOver<HotelRoom> {
  private RatingBar crimeBar;
  private RatingBar dirtLevelBar;


  public HotelRoomPopOver(final HotelRoom hotelRoom) {
    super(hotelRoom);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    crimeBar = makeStarRatingBar("Crime");
    crimeBar.setTextures(RatingBar.SECURITY_ICON);

    dirtLevelBar = makeStarRatingBar("Dirt");
    dirtLevelBar.setTextures(RatingBar.COCKROACH_ICON);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Manage", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new ManageHotelRoomDialog(gridObject).show();
      }
    });
    buttonBar.addButton("Redecorate", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new ConfirmRedecorationDialog(gridObject).show();
      }
    });

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

    crimeBar.setValue(gridObject.getCrimeLevel() * 5f);
    dirtLevelBar.setValue(gridObject.getDirtLevel() * 5f);
  }
}
