/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.Strings;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.platform.Platform;

import static com.happydroids.droidtowers.platform.Display.scale;

public class AboutWindow extends ScrollableTowerWindow {
  public AboutWindow(Stage stage) {
    super("Droid Towers by happydroids.com", stage);

    defaults().left().space(scale(8));

    row().padTop(scale(20));
    add(FontManager.RobotoBold18.makeLabel("Credits"));
    row();
    add(new HorizontalRule());

    addLabel("Philip Plante", FontManager.Roboto24);
    addLabel("Programming and Game Design", FontManager.Roboto18).spaceBottom(scale(16));

    addLabel("Will Phillips", FontManager.Roboto24);
    addLabel("Music Composer", FontManager.Roboto18);
    row();
    TextButton willPhillipsButton = FontManager.Roboto18.makeTextButton("facebook.com/willphillipsmusic");
    willPhillipsButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser("http://www.facebook.com/willphillipsmusic");
      }
    });
    add(willPhillipsButton).spaceBottom(scale(16));

    addLabel("Alex Miller", FontManager.Roboto24);
    addLabel("Lead Artist", FontManager.Roboto18).spaceBottom(scale(32));

    addLabel("Thank you to the following Friends who helped test:", FontManager.RobotoBold18);
    row();
    add(new HorizontalRule(Colors.DARK_GRAY, 1));
    addLabel(Strings.wrap(Gdx.files.internal("testers.txt").readString(), 60), FontManager.Roboto18);

    row().padTop(scale(50));
    add(new HorizontalRule(Colors.DARK_GRAY, 1));
    addLabel("Device ID: " + TowerGameService.instance().getDeviceId(), FontManager.Roboto18);
    addLabel("Game Version: " + HappyDroidConsts.VERSION + " (" + HappyDroidConsts.GIT_SHA.substring(0, 8) + ")", FontManager.Roboto18);

    row().spaceTop(scale(40));
    add(FontManager.RobotoBold18.makeLabel("Software Licenses"));
    row();
    add(new HorizontalRule());

    for (FileHandle fileHandle : Gdx.files.internal("licenses/").list(".txt")) {
      addLabel(fileHandle.readString(), FontManager.Roboto18).spaceBottom(scale(32));
    }

    shoveContentUp();
  }

  private Cell addLabel(String text, FontManager labelFont) {
    row();
    return add(labelFont.makeLabel(text));
  }
}
