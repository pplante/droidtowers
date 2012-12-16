/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.MovieTheater;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.gui.controls.ButtonBar;
import com.happydroids.droidtowers.gui.dialogs.CousinVinnieRepayLoanDialog;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.server.Movie;
import com.happydroids.droidtowers.utils.StringUtils;
import com.happydroids.platform.Platform;

public class MovieTheaterPopOver extends GridObjectPopOver {
  public MovieTheaterPopOver(MovieTheater movieTheater) {
    super(movieTheater);
  }

  @Override
  protected void buildControls() {
    super.buildControls();

    ButtonBar buttonBar = new ButtonBar();
    if (gridObject.hasLoanFromCousinVinnie()) {
      buttonBar.addButton("Repay Vinnie", new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          new CousinVinnieRepayLoanDialog((Room) gridObject).show();
        }
      });
    }

    Movie movie = ((MovieTheater) gridObject).getMovie();
    if (movie != null) {
      makeYouTubeTrailerButton(buttonBar, movie);
      makeMovieTicketsPurchaseButton(buttonBar, movie);
    }

    row().fillX().pad(Display.devicePixel(-8)).padTop(Display.devicePixel(16));
    add(buttonBar).expandX().minWidth(200);
  }

  private void makeYouTubeTrailerButton(ButtonBar buttonBar, Movie movie) {
    final String youtubeTrailerUrl = movie.getYoutubeTrailerUrl();
    if (!StringUtils.isEmpty(youtubeTrailerUrl)) {
      buttonBar.addButton("Watch Trailer", new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          Platform.getBrowserUtil().launchWebBrowser(youtubeTrailerUrl);
        }
      });
    }
  }

  private void makeMovieTicketsPurchaseButton(ButtonBar buttonBar, Movie movie) {
    final String ticketsPurchaseUrl = movie.getTicketsPurchaseUrl();
    if (!StringUtils.isEmpty(ticketsPurchaseUrl)) {
      buttonBar.addButton("Get Tickets", new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {

          Platform.getBrowserUtil().launchWebBrowser(ticketsPurchaseUrl);
        }
      });
    }
  }
}
