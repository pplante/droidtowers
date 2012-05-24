/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_UnhappyrobotToDroidTowers;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridObjectState;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.jackson.TowerTypeIdResolver;
import com.happydroids.droidtowers.math.GridPoint;
import sk.seges.acris.json.server.migrate.JacksonTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "class")
@JsonTypeIdResolver(TowerTypeIdResolver.class)
public class GameSave {
  protected int fileGeneration;
  protected int fileFormat;
  protected String cloudSaveUri;
  protected String towerName;
  protected DifficultyLevel difficultyLevel;
  protected Player player;
  protected Vector3 cameraPosition;
  protected float cameraZoom;
  protected GridPoint gridSize;
  protected List<GridObjectState> gridObjects;
  protected HashMap<String, Integer> objectCounts;
  protected ArrayList<String> completedAchievements;
  private String baseFilename;
  private boolean newGame;
  private GameGrid gameGrid;
  private OrthographicCamera camera;
  private boolean saveToDiskDisabled;

  public GameSave() {
    newGame = false;
  }

  public GameSave(String towerName, DifficultyLevel difficultyLevel) {
    newGame = true;

    this.towerName = towerName;
    this.difficultyLevel = difficultyLevel;
    baseFilename = generateFilename();
    player = new Player(difficultyLevel.getStartingMoney());
    gridSize = new GridPoint(TowerConsts.GAME_GRID_START_SIZE, TowerConsts.GAME_GRID_START_SIZE);
  }

  public void attachToGame(GameGrid gameGrid, OrthographicCamera camera, CameraController cameraController) {
    this.gameGrid = gameGrid;
    this.camera = camera;

    gameGrid.clearObjects();
    gameGrid.setTowerName(towerName);
    gameGrid.setGridSize(gridSize.x, gridSize.y);
    gameGrid.updateWorldSize(true);

    Player.setInstance(player);

    if (cameraPosition != null) {
      camera.zoom = cameraZoom;
      cameraController.panTo(cameraPosition, false);
    }

    if (gridObjects != null) {
      for (GridObjectState gridObjectState : gridObjects) {
        gridObjectState.materialize(gameGrid);
      }
    }

    if (newGame) {
      TutorialEngine.instance().setEnabled(true);
      TutorialEngine.instance().moveToStepWhenReady("tutorial-welcome");
    } else {
      TutorialEngine.instance().completeAll();
      HeadsUpDisplay.instance().getAchievementButton().visible = true;
    }

    AchievementEngine.instance().loadCompletedAchievements(completedAchievements, gameGrid);
    AchievementEngine.instance().checkAchievements(gameGrid);

    newGame = false;
  }

  public void update() {
    gridSize = gameGrid.getGridSize();
    gridObjects = Lists.newArrayList();
    objectCounts = Maps.newHashMap();

    for (GridObject gridObject : gameGrid.getObjects()) {
      if (gridObject.isPlaced()) {
        gridObjects.add(new GridObjectState(gridObject));

        String objectName = gridObject.getGridObjectType().getName();
        if (!objectCounts.containsKey(objectName)) {
          objectCounts.put(objectName, 0);
        }

        objectCounts.put(objectName, objectCounts.get(objectName) + 1);
      }
    }
    completedAchievements = Lists.newArrayList();

    for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
      if (achievement.isCompleted() && achievement.hasGivenReward()) {
        completedAchievements.add(achievement.getId());
      }
    }

    cameraPosition = camera.position;
    cameraZoom = camera.zoom;

    player = Player.instance();

    fileGeneration += 1;
  }

  @SuppressWarnings("PointlessBooleanExpression")
  public void save(FileHandle gameFile) throws IOException {
    if (TowerConsts.DEBUG && saveToDiskDisabled) return;

    OutputStream stream = gameFile.write(false);
    TowerGameService.instance().getObjectMapper().writeValue(stream, this);
    stream.flush();
    stream.close();
  }

  public String getCloudSaveUri() {
    return cloudSaveUri;
  }

  public static String generateFilename() {
    return UUID.randomUUID().toString().replaceAll("-", "") + ".json";
  }

  public String getBaseFilename() {
    return baseFilename;
  }

  public static GameSave readFile(FileHandle fileHandle) throws Exception {
    return readFile(fileHandle.read(), fileHandle.name());
  }

  public static GameSave readFile(InputStream read, String fileName) {
    try {
      JacksonTransformer transformer = new JacksonTransformer(read, fileName);
      transformer.addTransform(Migration_GameSave_UnhappyrobotToDroidTowers.class);

      byte[] bytes = transformer.process();

      return TowerGameService.instance().getObjectMapper().readValue(bytes, GameSave.class);
    } catch (Exception e) {
      throw new RuntimeException("There was a problem parsing: " + fileName, e);
    }
  }

  public boolean isNewGame() {
    return newGame;
  }

  public void setCloudSaveUri(String cloudSaveUri) {
    this.cloudSaveUri = cloudSaveUri;
  }

  public int getFileGeneration() {
    return fileGeneration;
  }

  public String getTowerName() {
    return towerName;
  }

  public Player getPlayer() {
    return player;
  }

  public void disableSaving() {
    saveToDiskDisabled = true;
  }

  public void setNewGame(boolean newGame) {
    this.newGame = newGame;
  }

  public boolean hasGridObjects() {
    return gridObjects != null && !gridObjects.isEmpty();
  }
}
