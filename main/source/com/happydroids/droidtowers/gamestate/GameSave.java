/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridObjectState;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.jackson.TowerTypeIdResolver;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "class")
@JsonTypeIdResolver(TowerTypeIdResolver.class)
public class GameSave {
  protected Player player;
  protected Vector3 cameraPosition;
  protected float cameraZoom;
  protected GridPoint gridSize;
  protected List<GridObjectState> gridObjects;
  protected ArrayList<String> completedAchievements;
  private boolean newGame;
  private boolean saveToDiskDisabled;
  protected Metadata metadata;

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
  public static class Metadata {
    public int fileGeneration;
    public int fileFormat;
    public String cloudSaveUri;
    public String baseFilename;
    public String towerName;
    public DifficultyLevel difficultyLevel;

    public Metadata() {
      baseFilename = GameSaveFactory.generateFilename();
    }

    public Metadata(String towerName, DifficultyLevel difficultyLevel) {
      this();
      this.towerName = towerName;
      this.difficultyLevel = difficultyLevel;
    }
  }

  public GameSave() {
    newGame = false;
  }

  public GameSave(String towerName, DifficultyLevel difficultyLevel) {
    newGame = true;

    this.metadata = new Metadata(towerName, difficultyLevel);
    player = new Player(difficultyLevel.getStartingMoney());
    gridSize = new GridPoint(TowerConsts.GAME_GRID_START_SIZE, TowerConsts.GAME_GRID_START_SIZE);
  }

  public void attachToGame(GameGrid gameGrid, OrthographicCamera camera, CameraController cameraController) {
    gameGrid.clearObjects();
    gameGrid.setTowerName(metadata.towerName);
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

  public void update(OrthographicCamera camera, GameGrid gameGrid) {
    gridSize = gameGrid.getGridSize();
    gridObjects = Lists.newArrayList();

    for (GridObject gridObject : gameGrid.getObjects()) {
      if (gridObject.isPlaced()) {
        gridObjects.add(new GridObjectState(gridObject));
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

    metadata.fileGeneration += 1;
  }

  @JsonIgnore
  public String getCloudSaveUri() {
    return metadata.cloudSaveUri;
  }

  @JsonIgnore
  public String getBaseFilename() {
    return metadata.baseFilename;
  }

  @JsonIgnore
  public boolean isNewGame() {
    return newGame;
  }

  @JsonIgnore
  public void setCloudSaveUri(String cloudSaveUri) {
    metadata.cloudSaveUri = cloudSaveUri;
  }

  @JsonIgnore
  public int getFileGeneration() {
    return metadata.fileGeneration;
  }

  @JsonIgnore
  public String getTowerName() {
    return metadata.towerName;
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

  public boolean isSaveToDiskDisabled() {
    return saveToDiskDisabled;
  }

  @JsonIgnore
  public CloudGameSave getCloudGameSave() {
    return new CloudGameSave(metadata.cloudSaveUri);
  }
}
