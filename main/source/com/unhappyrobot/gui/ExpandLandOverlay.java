/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.gui.events.CameraControllerEvent;
import com.unhappyrobot.input.CameraController;

public class ExpandLandOverlay extends WidgetGroup {
  private static final int PADDING = 300;
  private final GameGrid gameGrid;
  private TextButton leftButton;
  private TextButton rightButton;
  private TextButton topButton;

  public ExpandLandOverlay(GameGrid _gameGrid, Skin guiSkin) {
    this.gameGrid = _gameGrid;
    CameraController.events().register(this);

    leftButton = new TextButton("Expand Land", guiSkin);
    leftButton.visible = false;
    leftButton.x = 5;
    leftButton.y = Gdx.graphics.getHeight() / 2;
    addActor(leftButton);

    rightButton = new TextButton("Expand Land", guiSkin);
    rightButton.visible = false;
    rightButton.x = Gdx.graphics.getWidth() - rightButton.width - 5;
    rightButton.y = Gdx.graphics.getHeight() / 2;
    addActor(rightButton);

    leftButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        gameGrid.setGridSize(gameGrid.getGridSize().x + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE, gameGrid.getGridSize().y);
        gameGrid.updateWorldSize();

        for (GridObject gridObject : gameGrid.getObjects()) {
          gridObject.setPosition(gridObject.getPosition().x + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE, gridObject.getPosition().y);
        }

        Vector3 cameraPosition = CameraController.instance().getCamera().position.cpy();
        CameraController.instance().getCamera().position.set(cameraPosition.x + (TowerConsts.GRID_UNIT_SIZE * TowerConsts.GAME_GRID_EXPAND_LAND_SIZE), cameraPosition.y, cameraPosition.z);
        CameraController.instance().panTo(0, CameraController.instance().getCamera().position.y, true);
      }
    });

    rightButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        gameGrid.setGridSize(gameGrid.getGridSize().x + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE, gameGrid.getGridSize().y);
        gameGrid.updateWorldSize();
        CameraController.instance().panTo(gameGrid.getWorldSize().x - 10, CameraController.instance().getCamera().position.y, true);
      }
    });
  }

  public float getPrefWidth() {
    return 0;
  }

  public float getPrefHeight() {
    return 0;
  }

  @Subscribe
  public void CameraController_onPan(CameraControllerEvent event) {
    leftButton.visible = event.position.x <= PADDING * event.zoom;
    rightButton.visible = event.position.x + (PADDING * event.zoom) >= gameGrid.getWorldSize().x;
  }
}
