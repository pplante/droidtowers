/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_UnhappyrobotToDroidTowers;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridObjectState;
import com.happydroids.droidtowers.input.CameraController;
import sk.seges.acris.json.server.migrate.JacksonTransformer;

import java.io.IOException;

public class NonInteractiveGameSave extends GameSave {
  public NonInteractiveGameSave(GameSave gameSave) {
    super();
    gridSize = gameSave.gridSize;
    gridObjects = gameSave.gridObjects;
  }

  @Override
  public void attachToGame(GameGrid gameGrid, OrthographicCamera camera, CameraController cameraController) {
    gameGrid.clearObjects();
    gameGrid.setTowerName(towerName);
    gameGrid.setGridSize(gridSize.x, gridSize.y);
    gameGrid.updateWorldSize(true);

    if (gridObjects != null) {
      for (GridObjectState gridObjectState : gridObjects) {
        gridObjectState.materialize(gameGrid);
      }
    }
  }

  @Override
  public void update() {

  }

  @Override
  public void save(FileHandle gameFile) throws IOException {

  }

  public static GameSave readFile(FileHandle fileHandle) throws Exception {
    try {
      JacksonTransformer transformer = new JacksonTransformer(fileHandle.read(), fileHandle.name());
      transformer.addTransform(Migration_GameSave_UnhappyrobotToDroidTowers.class);

      byte[] bytes = transformer.process();

      final GameSave gameSave = TowerGameService.instance().getObjectMapper().readValue(bytes, GameSave.class);


      return new NonInteractiveGameSave(gameSave);
    } catch (Exception e) {
      throw new RuntimeException("There was a problem parsing: " + fileHandle.name(), e);
    }
  }
}
