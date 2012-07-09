/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.droidtowers.entities.HotelRoom;
import com.happydroids.droidtowers.gui.controls.ButtonBar;

import static com.happydroids.droidtowers.platform.Display.scale;

public class HotelRoomPopOver extends GridObjectPopOver<HotelRoom> {
  private final StarRatingBar crimeBar;
  private final StarRatingBar cleanlinessBar;

  public HotelRoomPopOver(final HotelRoom hotelRoom) {
    super(hotelRoom);

    crimeBar = makeStarRatingBar("Crime");
    cleanlinessBar = makeStarRatingBar("Cleanliness");

    ButtonBar buttonBar = new ButtonBar();

    buttonBar.addButton("Manage", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ManageHotelRoomDialog(hotelRoom).show();
      }
    });

    buttonBar.addButton("Redecorate", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ConfirmRedecorationDialog(gridObject).show();
      }
    });

    row().fillX().pad(scale(-8)).padTop(scale(16));
    add(buttonBar).expandX().minWidth(200);
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    crimeBar.setValue(gridObject.getCrimeLevel() * 5f);
    cleanlinessBar.setValue(5 - gridObject.getNumVisitors());
  }
}
