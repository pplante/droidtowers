/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;
import com.happydroids.droidtowers.platform.Display;

public class RoomPopOver extends GridObjectPopOver<Room> {
  private RatingBar residencyBar;
  private RatingBar crimeBar;
  private Table residentImages;


  public RoomPopOver(Room room) {
    super(room);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    crimeBar = makeStarRatingBar("Crime");
    crimeBar.setTextures(RatingBar.SECURITY_ICON);

    residencyBar = makeStarRatingBar("Residents");

    residentImages = new Table();
    residentImages.defaults().pad(Display.devicePixel(2));

    row().fillX();
    add(residentImages).center();

    if (gridObject.hasLoanFromCousinVinnie()) {
      ButtonBar buttonBar = new ButtonBar();
      buttonBar.addButton("Repay loan from Vinnie", new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          new CousinVinnieRepayLoanDialog(gridObject).show();
        }
      });

      row().fillX().pad(Display.devicePixel(-8)).padTop(Display.devicePixel(16));
      add(buttonBar).expandX().minWidth(200);
    }
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    crimeBar.setValue(gridObject.getSurroundingCrimeLevel() * 5f);
    residencyBar.setValue(gridObject.getResidencyLevel() * 5f);

    boolean updatedLayout = false;

    if (gridObject.hasResidents()) {
      if (residentImages.getChildren().size < gridObject.getResidents().size()) {
        residentImages.clear();

        for (Avatar avatar : gridObject.getResidents()) {
          Image image = new Image(new TextureRegionDrawable(avatar), Scaling.none);
          image.getColor().set(avatar.getColor());
          residentImages.add(image).width((int) avatar.getWidth());
        }
        residentImages.pack();
        updatedLayout = true;
      }
    }

    if (updatedLayout) {
      invalidateHierarchy();
      pack();
    }
  }
}
