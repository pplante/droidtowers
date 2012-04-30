/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.Input.Keys.ESCAPE;

public class HeaderButtonBar extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.25f;
  public static final float BUTTON_FADE_DURATION = 0.25f;

  private final AudioControl audioControl;
  private final ImageButton dataOverlayButton;
  private final DataOverlayMenu dataOverlayMenu;
  private final ImageButton achievementsButton;

  public HeaderButtonBar(TextureAtlas hudAtlas, GameGrid gameGrid) {
    audioControl = new AudioControl(hudAtlas);
    dataOverlayButton = TowerAssetManager.imageButton(hudAtlas.findRegion("overlay-button"));
    achievementsButton = new ImageButton(TowerAssetManager.textureFromAtlas("achievements", "hud/buttons.txt"));

    dataOverlayMenu = new DataOverlayMenu(gameGrid.getRenderer());
    dataOverlayMenu.visible = false;

    defaults().space(6);
    row().right();
    add(achievementsButton);
    add(audioControl);
    add(dataOverlayButton);

    row().colspan(3).right().padTop(-dataOverlayMenu.getOffset());
    add(dataOverlayMenu);

    pack();
    debug();

    achievementsButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        new AchievementListView(getStage(), TowerAssetManager.getGuiSkin()).show();
      }
    });

    dataOverlayButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        toggleDataOverlayMenu();
      }
    });
  }

  private void toggleDataOverlayMenu() {
    dataOverlayMenu.visible = !dataOverlayMenu.visible;

    if (dataOverlayMenu.visible) {
      InputSystem.instance().bind(new int[]{ESCAPE, BACK}, dataOverlayMenuToggleCallback);
      InputSystem.instance().addInputProcessor(dataOverlayMenuToggleClickCallback, 0);
    } else {
      InputSystem.instance().unbind(new int[]{ESCAPE, BACK}, dataOverlayMenuToggleCallback);
      InputSystem.instance().removeInputProcessor(dataOverlayMenuToggleClickCallback);
    }

    dataOverlayButton.action(FadeTo.$(dataOverlayMenu.visible ? INACTIVE_BUTTON_ALPHA : 1f, BUTTON_FADE_DURATION));
    achievementsButton.action(FadeTo.$(dataOverlayMenu.visible ? INACTIVE_BUTTON_ALPHA : 1f, BUTTON_FADE_DURATION));
    audioControl.action(FadeTo.$(dataOverlayMenu.visible ? INACTIVE_BUTTON_ALPHA : 1f, BUTTON_FADE_DURATION));
  }

  private final InputCallback dataOverlayMenuToggleCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      boolean menuWasVisible = dataOverlayMenu.visible;
      toggleDataOverlayMenu();
      return menuWasVisible;
    }
  };

  private final InputAdapter dataOverlayMenuToggleClickCallback = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      Vector2 touchDown = new Vector2();
      getStage().toStageCoordinates(x, y, touchDown);
      dataOverlayMenu.toLocalCoordinates(touchDown);

      if (dataOverlayMenu.hit(touchDown.x, touchDown.y) == null) {
        toggleDataOverlayMenu();
        return true;
      }

      return false;
    }
  };
}
