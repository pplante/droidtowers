/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridObjectState;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.MINIMAL_CLASS;

@JsonAutoDetect(fieldVisibility = PROTECTED_AND_PUBLIC, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
@JsonTypeInfo(use = MINIMAL_CLASS, include = WRAPPER_OBJECT, property = "class")
//@JsonTypeIdResolver(TowerTypeIdResolver.class)
public class GameSave {
  @JsonView({Views.Metadata.class, Views.All.class})
  protected Player player;
  @JsonView(Views.All.class)
  protected Vector3 cameraPosition;
  @JsonView(Views.All.class)
  protected float cameraZoom;
  @JsonView(Views.All.class)
  protected GridPoint gridSize;
  @JsonView(Views.All.class)
  protected List<GridObjectState> gridObjects;
  @JsonView(Views.All.class)
  protected ArrayList<String> completedAchievements;
  @JsonView(Views.All.class)
  protected List<String> neighbors;
  private boolean newGame;

  private boolean saveToDiskDisabled;
  @JsonView({Views.Metadata.class, Views.All.class})
  protected GameSaveMetadata metadata;
  protected int fileFormat = 4;

  public GameSave() {
    newGame = false;
  }

  public GameSave(String towerName, DifficultyLevel difficultyLevel) {
    newGame = true;

    this.metadata = new GameSaveMetadata(towerName, difficultyLevel);
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
      TutorialEngine.instance().setEnabled(false);
      TutorialEngine.instance().completeAll();
      HeadsUpDisplay.instance().getAchievementButton().setVisible(true);
      HeadsUpDisplay.instance().toggleViewNeighborsButton(true);
    }

    AchievementEngine.instance().loadCompletedAchievements(completedAchievements, gameGrid);
    AchievementEngine.instance().checkAchievements(gameGrid);

    newGame = false;
  }

  public void update(OrthographicCamera camera, GameGrid gameGrid, List<String> neighbors) {
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
    this.neighbors = neighbors;
    metadata.fileGeneration += 1;
  }

  public String getCloudSaveUri() {
    return metadata.cloudSaveUri;
  }

  public String getBaseFilename() {
    return metadata.baseFilename;
  }

  public boolean isNewGame() {
    return newGame;
  }

  public void setCloudSaveUri(String cloudSaveUri) {
    metadata.cloudSaveUri = cloudSaveUri;
  }

  public int getFileGeneration() {
    return metadata.fileGeneration;
  }

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

  public GameSaveMetadata getMetadata() {
    return metadata;
  }

  public void setNeighbors(List<String> neighbors) {
    this.neighbors = neighbors;
  }

  public int numNeighbors() {
    if (neighbors != null) {
      return neighbors.size();
    }

    return 0;
  }

  public static class Views {
    public static class Metadata {
    }

    public static class All {
    }
  }
}
