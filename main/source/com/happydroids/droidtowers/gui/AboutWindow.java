/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.utils.StringUtils;
import com.happydroids.platform.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AboutWindow extends ScrollableTowerWindow {
  public AboutWindow(Stage stage) {
    super("Droid Towers by happydroids.com", stage);

    defaults().left().space(Display.devicePixel(8));

    row().padTop(Display.devicePixel(20));
    add(FontManager.RobotoBold18.makeLabel("Credits"));
    addHorizontalRule(Colors.DARK_GRAY, 1, 1);

    addLabel("Philip Plante", FontManager.Roboto24);
    addLabel("Programming and Game Design", FontManager.Roboto18).spaceBottom(Display.devicePixel(16));

    addLabel("Will Phillips", FontManager.Roboto24);
    addLabel("Music Composer", FontManager.Roboto18);
    row();
    TextButton willPhillipsButton = FontManager.Roboto18.makeTextButton("facebook.com/willphillipsmusic");
    willPhillipsButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        Platform.getBrowserUtil().launchWebBrowser("http://www.facebook.com/willphillipsmusic");
      }
    });
    add(willPhillipsButton).spaceBottom(Display.devicePixel(16));

    addLabel("Alex Miller", FontManager.Roboto24);
    addLabel("Lead Artist", FontManager.Roboto18).spaceBottom(Display.devicePixel(32));

    addLabel("Thank you to the following Friends who helped test:", FontManager.RobotoBold18);
    addHorizontalRule(Colors.DARK_GRAY, 1, 1);
    addLabel(StringUtils.wrap(Gdx.files.internal("testers.txt").readString(), 60), FontManager.Roboto18);

    addHorizontalRule(Colors.DARK_GRAY, 1, 1).padTop(Display.devicePixel(50));
    addLabel("Device ID: " + TowerGameService.instance().getDeviceId(), FontManager.Roboto18);
    addLabel("Game Version: " + HappyDroidConsts.VERSION + " (" + HappyDroidConsts.GIT_SHA
                                                                          .substring(0, 8) + ")", FontManager.Roboto18);

    row().spaceTop(Display.devicePixel(40));
    add(FontManager.RobotoBold18.makeLabel("Software Licenses"));
    addHorizontalRule(Colors.DARK_GRAY, 1, 1);

    try {
      ObjectMapper mapper = new ObjectMapper();
      List<String> licenseFiles = mapper.readValue(Gdx.files
                                                           .internal("licenses/index.json")
                                                           .readBytes(), mapper.getTypeFactory()
                                                                                 .constructCollectionType(ArrayList.class, String.class));
      for (String licenseFile : licenseFiles) {
        FileHandle licenseFileHandle = Gdx.files.internal(licenseFile);
        if (licenseFileHandle.exists()) {
          addLabel(licenseFileHandle.readString(), FontManager.Roboto18).spaceBottom(Display.devicePixel(32));
        }
      }
    } catch (IOException ignored) {
    }
  }
}
