package com.unhappyrobot.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.collect.Lists;
import com.unhappyrobot.achievements.Achievement;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.GridObjectPlacementState;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridObjectState;
import com.unhappyrobot.jackson.Vector2Serializer;
import com.unhappyrobot.jackson.Vector3Serializer;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "class")
public class GameSave {
  protected Player player;
  protected List<GridObjectState> gridObjects;
  protected HashMap<String, Integer> objectCounts;

  protected Vector3 cameraPosition;
  protected float cameraZoom;
  protected ArrayList<String> completedAchievements;
  protected String cloudSaveUri;
  protected Vector2 gridSize;


  public GameSave() {

  }

  public GameSave(GameGrid gameGrid, OrthographicCamera camera, Player player) {
    this.player = player;
    cameraPosition = camera.position;
    cameraZoom = camera.zoom;

    gridSize = gameGrid.gridSize;
    gridObjects = Lists.newArrayList();

    for (GridObject gridObject : gameGrid.getObjects()) {
      if (gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
        gridObjects.add(new GridObjectState(gridObject));

        String objectName = gridObject.getGridObjectType().getName();
        if (!objectCounts.containsKey(objectName)) {
          objectCounts.put(objectName, 0);
        }

        objectCounts.put(objectName, objectCounts.get(objectName) + 1);
      }
    }

    completedAchievements = Lists.newArrayList();

    for (Achievement achievement : AchievementEngine.instance().getCompletedAchievements()) {
      completedAchievements.add(achievement.getId());
    }
  }

  public static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule("Specials", new Version(1, 0, 0, null));
    simpleModule.addSerializer(new Vector3Serializer());
    simpleModule.addSerializer(new Vector2Serializer());
    objectMapper.registerModule(simpleModule);
    return objectMapper;
  }


  public Player getPlayer() {
    return player;
  }

  public Vector3 getCameraPosition() {
    return cameraPosition;
  }

  public float getCameraZoom() {
    return cameraZoom;
  }

  public List<GridObjectState> getGridObjects() {
    return gridObjects;
  }

  public List<String> getCompletedAchievements() {
    return completedAchievements;
  }

  public void setCloudSaveUri(String cloudSaveUri) {
    this.cloudSaveUri = cloudSaveUri;
  }

  public String getCloudSaveUri() {
    return cloudSaveUri;
  }

  public Vector2 getGridSize() {
    return gridSize;
  }
}
