/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.droidtowers.entities.MovieTheater;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.platform.Platform;

import static com.happydroids.droidtowers.platform.Display.scale;

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
      public void onClick(Actor actor, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser(((MovieTheater) gridObject).getMovie().getYoutubeTrailerUrl());
      }
    });
    buttonBar.addButton("Get Tickets", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser(((MovieTheater) gridObject).getMovie().getTicketsPurchaseUrl());
      }
    });

    row().fillX().pad(scale(-8)).padTop(scale(16));
    add(buttonBar).expandX().minWidth(200);
  }
}
