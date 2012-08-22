/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Pools;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.events.CameraControllerEvent;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.Platform;

import static com.happydroids.droidtowers.TowerConsts.GAME_GRID_EXPAND_LAND_SIZE;

public class ExpandLandOverlay extends WidgetGroup {
  private static final int PADDING = 300;
  private final GameGrid gameGrid;
  private final AvatarLayer avatarLayer;
  private final CameraController cameraController;
  private Button leftButton;
  private Button rightButton;

  public ExpandLandOverlay(GameGrid gameGrid, AvatarLayer avatarLayer, CameraController cameraController) {
    this.gameGrid = gameGrid;
    this.avatarLayer = avatarLayer;
    this.cameraController = cameraController;
    cameraController.events().register(this);

    leftButton = new ExpandLandButton("left");
    leftButton.setVisible(false);
    leftButton.setX(5);
    leftButton.setY((Gdx.graphics.getHeight() - leftButton.getHeight()) / 2);
    addActor(leftButton);

    rightButton = new ExpandLandButton("right");
    rightButton.setVisible(false);
    rightButton.setX(Display.getWidth() - rightButton.getWidth() - 5);
    rightButton.setY((Display.getHeight() - rightButton.getHeight()) / 2);
    addActor(rightButton);

    leftButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        expandLandToWest();
      }
    });

    rightButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        expandLandToEast();
      }
    });
  }

  private void expandLandToEast() {
    if (!Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
      new PurchaseDroidTowersUnlimitedPrompt().show();
      return;
    }

    gameGrid.events().unregister(DroidTowersGame.getSoundController());
    gameGrid.getGridSize().x += GAME_GRID_EXPAND_LAND_SIZE;
    gameGrid.updateWorldSize(false);

    for (GridObject gridObject : gameGrid.getObjects()) {
      GridObjectBoundsChangeEvent event = Pools.obtain(GridObjectBoundsChangeEvent.class);
      event.setGridObject(gridObject);

      gridObject.setPosition(gridObject.getPosition());

      gridObject.broadcastEvent(event);
      Pools.free(event);
    }

    cameraController.panTo(gameGrid.getWorldSize().x, cameraController.getCamera().position.y, true);
    gameGrid.events().register(DroidTowersGame.getSoundController());
  }

  private void expandLandToWest() {
    if (!Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
      new PurchaseDroidTowersUnlimitedPrompt().show();
      return;
    }

    gameGrid.events().unregister(DroidTowersGame.getSoundController());
    gameGrid.getGridSize().x += GAME_GRID_EXPAND_LAND_SIZE;
    gameGrid.updateWorldSize(false);

    for (GridObject gridObject : gameGrid.getObjects()) {
      GridPoint position = gridObject.getPosition();
      gridObject.setPosition(position.x + GAME_GRID_EXPAND_LAND_SIZE, position.y);
      gridObject.adjustToNewLandSize();
    }
    avatarLayer.adjustAvatarPositions(GAME_GRID_EXPAND_LAND_SIZE);

    Vector3 cameraPosition = cameraController.getCamera().position.cpy();
    cameraController.getCamera()
            .position
            .set(cameraPosition.x + (TowerConsts.GRID_UNIT_SIZE * GAME_GRID_EXPAND_LAND_SIZE), cameraPosition.y, cameraPosition.z);
    cameraController.panTo(0, cameraController.getCamera().position.y, true);
    gameGrid.events().register(DroidTowersGame.getSoundController());
  }

  public float getPrefWidth() {
    return 0;
  }

  public float getPrefHeight() {
    return 0;
  }

  private static class ExpandLandButton extends Button {

    public ExpandLandButton(String textureSuffix) {
      super(new ButtonStyle(
                                   makeNinePatch(textureSuffix, new Color(1f, 1f, 1f, 0.5f)),
                                   makeNinePatch(textureSuffix, Colors.ICS_BLUE),
                                   makeNinePatch(textureSuffix, new Color(1f, 1f, 1f, 0.75f))));
    }

    private static NinePatchDrawable makeNinePatch(String textureSuffix, Color color) {
      return new NinePatchDrawable(new NinePatch(TowerAssetManager.textureFromAtlas("expand-land-" + textureSuffix, "hud/buttons.txt"), color));
    }

  }

  @Subscribe
  public void CameraController_onPan(CameraControllerEvent event) {
    leftButton.setVisible(event.getPosition().x <= PADDING * event.getZoom());
    rightButton.setVisible(event.getPosition().x + (PADDING * event.getZoom()) >= gameGrid.getWorldSize().x);
  }
}
