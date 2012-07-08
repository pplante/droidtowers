/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.ManageServiceRoomDialog;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.gui.controls.ButtonBar;

public class ServiceRoomPopOver extends GridObjectPopOver<ServiceRoom> {
  public ServiceRoomPopOver(final ServiceRoom serviceRoom) {
    super(serviceRoom);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Manage", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new ManageServiceRoomDialog(serviceRoom).show();
      }
    });

    row().fillX();
    add(buttonBar).expandX();
  }
}
