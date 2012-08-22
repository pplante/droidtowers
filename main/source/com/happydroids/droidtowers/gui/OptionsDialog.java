/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.security.SecurePreferences;

import javax.annotation.Nullable;
import java.util.List;

public class OptionsDialog extends Dialog {
  private final SecurePreferences preferences;
  private final CheckBox fullscreenCheckbox;
  private SelectBox displayResolution;
  private List<Graphics.DisplayMode> displayModeList;
  private boolean displayModeChanged;

  public OptionsDialog(Stage stage) {
    super(stage);

    preferences = TowerGameService.instance().getPreferences();
    fullscreenCheckbox = FontManager.RobotoBold18.makeCheckBox("Fullscreen");
    fullscreenCheckbox.setChecked(Gdx.graphics.isFullscreen());

    setTitle("Options");

    Table c = new Table();
    c.defaults().top().left().space(Display.devicePixel(16));

    c.row().fillX();
    c.add(FontManager.Default.makeLabel("Music Volume"));
    c.add(makeMusicVolumeSlider());

    c.row().fillX();
    c.add(FontManager.Default.makeLabel("Effects Volume"));
    c.add(makeSoundEffectsVolumeSlider());

    if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
      c.row().fillX();
      c.add();
      c.add(makeHapticFeedbackCheckbox());
    }

    if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
      c.row().fillX();
      c.add(FontManager.RobotoBold18.makeLabel("Resolution: "));
      c.add(makeResolutionSelectBox());

      fullscreenCheckbox.addListener(new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          saveDisplayChanges(displayModeList.get(displayResolution.getSelectionIndex()));
        }
      });
      c.row();
      c.add();
      c.add(fullscreenCheckbox);

      setDismissCallback(new Runnable() {
        @Override
        public void run() {
          if (displayModeChanged) {
            Gdx.graphics
                    .setDisplayMode(preferences.getInteger("width"), preferences.getInteger("height"), preferences.getBoolean("fullscreen"));
            SceneManager.restartActiveScene();
          }
        }
      });
    }

    setView(c);
  }

  private CheckBox makeHapticFeedbackCheckbox() {
    final CheckBox checkBox = FontManager.Roboto18.makeCheckBox("Vibrate on touch");
    checkBox.setChecked(VibrateClickListener.isVibrateEnabled());
    checkBox.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        VibrateClickListener.setVibrateEnabled(checkBox.isChecked());
        preferences.putBoolean("vibrateOnTouch", checkBox.isChecked());
        preferences.flush();
      }
    });
    return checkBox;
  }

  private Slider makeMusicVolumeSlider() {
    final Slider slider = new Slider(0f, 1f, 0.1f, false, TowerAssetManager.getCustomSkin());
    slider.setValue(DroidTowersGame.getSoundController().getMusicVolume());
    slider.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        DroidTowersGame.getSoundController().setMusicVolume(slider.getValue());
      }
    });
    return slider;
  }

  private Slider makeSoundEffectsVolumeSlider() {
    final Slider slider = new Slider(0f, 1f, 0.1f, false, TowerAssetManager.getCustomSkin());
    slider.setValue(DroidTowersGame.getSoundController().getEffectsVolume());
    slider.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        preferences.putFloat("effectsVolume", slider.getValue());
        DroidTowersGame.getSoundController().setEffectsVolume(slider.getValue());
      }
    });
    return slider;
  }

  private SelectBox makeResolutionSelectBox() {
    List<Graphics.DisplayMode> displayModes = Lists.newArrayList();
    for (Graphics.DisplayMode displayMode : Gdx.graphics.getDisplayModes()) {
      if (displayMode.width > 800 && displayMode.height > 480) {
        displayModes.add(displayMode);
      }
    }

    displayModeList = Ordering.natural().onResultOf(new Function<Graphics.DisplayMode, Comparable>() {
      @Override
      public Comparable apply(@Nullable Graphics.DisplayMode input) {
        return input.width * input.height * input.bitsPerPixel;
      }
    }).sortedCopy(Lists.newArrayList(displayModes));

    List<String> displayModeStrings = Lists.newArrayList();
    for (Graphics.DisplayMode displayMode : displayModeList) {
      displayModeStrings.add(displayMode.width + "x" + displayMode.height);
    }

    displayResolution = new SelectBox(displayModeStrings.toArray(), TowerAssetManager.getCustomSkin());
    int currentResolution = displayModeStrings.indexOf(Display.getWidth() + "x" + Gdx.graphics.getHeight());
    if (currentResolution > -1) {
      displayResolution.setSelection(currentResolution);
    }

    displayResolution.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
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
    preferences.putInteger("width", displayMode.width);
    preferences.putInteger("height", displayMode.height);
    preferences.putBoolean("fullscreen", fullscreenCheckbox.isChecked());
    preferences.flush();
  }

}
