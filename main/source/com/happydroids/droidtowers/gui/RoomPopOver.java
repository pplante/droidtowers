/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.Room;

public class RoomPopOver extends GridObjectPopOver {
  private final StarRatingBar residencyBar;
  private final Image avatarImage;

  public RoomPopOver(Room room) {
    super(room);

    residencyBar = makeStarRatingBar("Residents");

    row().fillX();
    avatarImage = new Image();
    avatarImage.setScaling(Scaling.none);
    add(avatarImage).center();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    residencyBar.setValue(room.getResidencyLevel() * 5f);

    if (room.hasResident() && avatarImage.getRegion() == null) {
      Avatar avatar = room.getResident();
      avatarImage.setRegion(avatar);
      avatarImage.color.set(avatar.getColor());
      invalidateHierarchy();
      pack();
    }
  }
}
