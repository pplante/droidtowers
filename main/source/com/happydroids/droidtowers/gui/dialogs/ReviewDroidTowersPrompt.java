/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.platform.Platform;
import com.happydroids.security.SecurePreferences;

import static com.badlogic.gdx.Application.ApplicationType.Applet;

public class ReviewDroidTowersPrompt extends Dialog {
  public static final String RATING_ADDED = "RatingAdded";
  public static final String RATING_TIMES_SINCE_PROMPTED = "RatingTimesSincePrompted";
  public static final String RATING_NEVER_ASK_AGAIN = "RatingNeverAskAgain";

  public ReviewDroidTowersPrompt(Stage stage) {
    super(stage);

    String ratingVerb = Gdx.app.getType().equals(Applet) ? "like" : "rate";

    setMessage("Hello there!\n\nYou have been playing Droid Towers, and we're sure you have some feedback for us.\n\nWould you " + ratingVerb + " our app?");

    addButton("Sure", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        gotoMarketForRating();
      }
    });

    addButton("Maybe later", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        resetCounter();
      }
    });

    addButton("Never ask again", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        neverAskAgain();
      }
    });
  }

  private void gotoMarketForRating() {
    if (Gdx.app.getType().equals(Applet)) {
      Platform.getBrowserUtil().launchWebBrowser("http://www.facebook.com/droidtowers");
    } else if (TowerGameService.getDeviceOSMarketName().equalsIgnoreCase("google-play")) {

    } else if (TowerGameService.getDeviceOSMarketName().equalsIgnoreCase("google-play")) {

    }
    SecurePreferences preferences = TowerGameService.instance().getPreferences();
    preferences.putBoolean(RATING_ADDED, true);
    preferences.flush();

    dismiss();
  }

  private void neverAskAgain() {
    SecurePreferences preferences = TowerGameService.instance().getPreferences();
    preferences.putBoolean(RATING_NEVER_ASK_AGAIN, true);
    preferences.flush();

    dismiss();
  }

  private void resetCounter() {
    SecurePreferences preferences = TowerGameService.instance().getPreferences();
    preferences.putInteger(RATING_TIMES_SINCE_PROMPTED, 0);
    preferences.flush();

    dismiss();
  }
}
