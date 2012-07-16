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
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import javax.annotation.Nullable;
import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class OptionsDialog extends Dialog {
  private final Preferences displayPrefs;
  private final CheckBox fullscreenCheckbox;
  private SelectBox displayResolution;
  private List<Graphics.DisplayMode> displayModeList;
  private boolean displayModeChanged;

  public OptionsDialog(Stage stage) {
    super(stage);

    setTitle("Options");

    Table body = new Table();
    body.defaults().top().left().space(scale(16));

    body.row().fillX();
    body.add(FontManager.RobotoBold18.makeLabel("Resolution: "));
    body.add(makeResolutionSelectBox());

    fullscreenCheckbox = FontManager.RobotoBold18.makeCheckBox("Fullscreen");
    fullscreenCheckbox.setChecked(Gdx.graphics.isFullscreen());
    fullscreenCheckbox.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        saveDisplayChanges(displayModeList.get(displayResolution.getSelectionIndex()));
      }
    });
    body.row();
    body.add();
    body.add(fullscreenCheckbox);

    displayPrefs = TowerGameService.instance().getPreferences();

    setView(body);

    setDismissCallback(new Runnable() {
      @Override
      public void run() {
        if (displayModeChanged) {
          Gdx.graphics.setDisplayMode(displayPrefs.getInteger("width"), displayPrefs.getInteger("height"), displayPrefs.getBoolean("fullscreen"));
          SceneManager.restartActiveScene();
        }
      }
    });
  }

  private SelectBox makeResolutionSelectBox() {
    displayModeList = Ordering.natural().onResultOf(new Function<Graphics.DisplayMode, Comparable>() {
      @Override
      public Comparable apply(@Nullable Graphics.DisplayMode input) {
        return input.width * input.height * input.bitsPerPixel;
      }
    }).sortedCopy(Lists.newArrayList(Gdx.graphics.getDisplayModes()));

    List<String> displayModeStrings = Lists.newArrayList();
    for (Graphics.DisplayMode displayMode : displayModeList) {
      displayModeStrings.add(displayMode.width + "x" + displayMode.height);
    }

    displayResolution = new SelectBox(displayModeStrings.toArray(), TowerAssetManager.getCustomSkin());
    displayResolution.setSelection(displayModeStrings.indexOf(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight()));

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
    displayModeChanged = true;
    displayPrefs.putInteger("width", displayMode.width);
    displayPrefs.putInteger("height", displayMode.height);
    displayPrefs.putBoolean("fullscreen", fullscreenCheckbox.isChecked());
    displayPrefs.flush();
  }

}
