/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectionListener;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.TowerAssetManager;

import javax.annotation.Nullable;
import java.util.List;

public class OptionsWindow extends TowerWindow {

  public OptionsWindow(Stage stage) {
    super("Options", stage);

    Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();

    final List<Graphics.DisplayMode> displayModeList = Ordering.natural().onResultOf(new Function<Graphics.DisplayMode, Comparable>() {
      @Override
      public Comparable apply(@Nullable Graphics.DisplayMode input) {
        return input.width * input.height * input.bitsPerPixel;
      }
    }).sortedCopy(Lists.newArrayList(displayModes));

    SelectBox displayResolution = new SelectBox(displayModeList.toArray(), TowerAssetManager.getDefaultSkin());
    row();
    add(displayResolution);

    displayResolution.setSelection(Gdx.graphics.getDesktopDisplayMode().toString());

    displayResolution.setSelectionListener(new SelectionListener() {
      @Override
      public void selected(Actor actor, int index, String value) {
        Graphics.DisplayMode displayMode = displayModeList.get(index);

        Preferences displayPrefs = Gdx.app.getPreferences("DISPLAY");
        displayPrefs.putInteger("width", displayMode.width);
        displayPrefs.putInteger("height", displayMode.height);
        displayPrefs.flush();
      }
    });
  }
}
