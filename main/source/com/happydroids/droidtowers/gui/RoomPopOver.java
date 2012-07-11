/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;

import static com.happydroids.droidtowers.platform.Display.scale;

public class RoomPopOver extends GridObjectPopOver<Room> {
  private StarRatingBar residencyBar;
  private StarRatingBar crimeBar;
  private Table residentImages;


  public RoomPopOver(Room room) {
    super(room);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    crimeBar = makeStarRatingBar("Crime");
    residencyBar = makeStarRatingBar("Residents");

    residentImages = new Table();
    residentImages.defaults().pad(scale(2));

    row().fillX();
    add(residentImages).center();

    if (gridObject.hasLoanFromCousinVinnie()) {
      ButtonBar buttonBar = new ButtonBar();
      buttonBar.addButton("Repay loan from Vinnie", new VibrateClickListener() {
        @Override
        public void onClick(Actor actor, float x, float y) {
          new CousinVinnieRepayLoanDialog(gridObject).show();
        }
      });

      row().fillX().pad(scale(-8)).padTop(scale(16));
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
      if (residentImages.getActors().size() < gridObject.getResidents().size()) {
        residentImages.clear();

        for (Avatar avatar : gridObject.getResidents()) {
          Image image = new Image(avatar, Scaling.none);
          image.color.set(avatar.getColor());
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
