/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.entities.MovieTheater;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.Platform;

public class MovieTheaterPopOver extends GridObjectPopOver {
  public MovieTheaterPopOver(MovieTheater movieTheater) {
    super(movieTheater);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.addButton("Watch Trailer", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser(((MovieTheater) gridObject).getMovie().getYoutubeTrailerUrl());
      }
    });
    buttonBar.addButton("Get Tickets", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser(((MovieTheater) gridObject).getMovie().getTicketsPurchaseUrl());
      }
    });

    row().fillX().pad(Display.devicePixel(-8)).padTop(Display.devicePixel(16));
    add(buttonBar).expandX().minWidth(200);
  }
}
