/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridObjectState;
import com.happydroids.droidtowers.input.CameraController;

import java.util.List;

public class NonInteractiveGameSave extends GameSave {
  public NonInteractiveGameSave(GameSave gameSave) {
    super();
    metadata = gameSave.metadata;
    gridSize = gameSave.gridSize;
    gridObjects = gameSave.gridObjects;
  }

  @Override
  public void attachToGame(GameGrid gameGrid, OrthographicCamera camera, CameraController cameraController) {
    gameGrid.clearObjects();
    gameGrid.setTowerName(metadata.towerName);
    gameGrid.setGridSize(gridSize.x, gridSize.y);
    gameGrid.updateWorldSize(true);

    if (gridObjects != null) {
      for (GridObjectState gridObjectState : gridObjects) {
        gridObjectState.materialize(gameGrid);
      }
    }
  }

  @Override
  public void update(OrthographicCamera camera, GameGrid gameGrid, List<String> neighbors) {

  }
}
