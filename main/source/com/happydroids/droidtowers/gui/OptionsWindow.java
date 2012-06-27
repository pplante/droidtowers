/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectionListener;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.TowerAssetManager;

import javax.annotation.Nullable;
import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class OptionsWindow extends ScrollableTowerWindow {

  private final Preferences displayPrefs;
  private final CheckBox fullscreenCheckbox;
  private SelectBox displayResolution;
  private List<Graphics.DisplayMode> displayModeList;

  public OptionsWindow(Stage stage) {
    super("Options", stage);

    defaults().top().left().space(scale(16));

    row().padTop(scale(16));
    add(FontManager.Roboto24.makeLabel("Display Settings")).colspan(3);
    row();
    add(new HorizontalRule()).colspan(3);

    row().fillX();
    add(FontManager.RobotoBold18.makeLabel("Resolution: "));
    add(makeResolutionSelectBox());
    add(FontManager.Roboto12.makeLabel("* requires restart")).expandX();

    fullscreenCheckbox = FontManager.RobotoBold18.makeCheckBox("Fullscreen");
    fullscreenCheckbox.setChecked(Gdx.graphics.isFullscreen());
    fullscreenCheckbox.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        saveDisplayChanges(displayModeList.get(displayResolution.getSelectionIndex()));
      }
    });
    row();
    add();
    add(fullscreenCheckbox);
    add(FontManager.Roboto12.makeLabel("* requires restart"));

    shoveContentUp(3);
    displayPrefs = Gdx.app.getPreferences("DISPLAY");
  }

  private SelectBox makeResolutionSelectBox() {
    Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();

    displayModeList = Ordering.natural().onResultOf(new Function<Graphics.DisplayMode, Comparable>() {
      @Override
      public Comparable apply(@Nullable Graphics.DisplayMode input) {
        return input.width * input.height * input.bitsPerPixel;
      }
    }).sortedCopy(Lists.newArrayList(displayModes));

    List<String> displayModeStrings = Lists.newArrayList();
    for (Graphics.DisplayMode displayMode : displayModeList) {
      displayModeStrings.add(formatDisplayMode(displayMode));
    }

    displayResolution = new SelectBox(displayModeStrings.toArray(), TowerAssetManager.getCustomSkin());
    displayResolution.setSelection(displayModeStrings.indexOf(formatDisplayMode(Gdx.graphics.getDesktopDisplayMode())));

    displayResolution.setSelectionListener(new SelectionListener() {
      @Override
      public void selected(Actor actor, int index, String value) {
        saveDisplayChanges(displayModeList.get(displayResolution.getSelectionIndex()));
      }
    });

    return displayResolution;
  }

  private String formatDisplayMode(Graphics.DisplayMode displayMode) {
    return displayMode.width + "x" + displayMode.height;
  }

  private void saveDisplayChanges(Graphics.DisplayMode displayMode) {
    displayPrefs.putInteger("width", displayMode.width);
    displayPrefs.putInteger("height", displayMode.height);
    displayPrefs.putBoolean("fullscreen", fullscreenCheckbox.isChecked());
    displayPrefs.flush();
  }
}
