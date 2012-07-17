/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.collections.TypeInstanceMap;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.events.GridObjectAddedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.happydroids.droidtowers.TowerConsts.SINGLE_POINT;


public class GameGrid extends GameLayer {
  private EventBus eventBus = new EventBus(GameGrid.class.getSimpleName());

  protected float gridScale;
  protected GridPoint gridSize;
  private int highestPoint;
  private GridPoint gridOrigin;
  private Vector2 worldSize;
  protected Rectangle worldBounds;
  protected GameGridRenderer gameGridRenderer;
  protected GridPositionCache positionCache;
  private TypeInstanceMap<GridObject> gridObjects;
  private GridObject selectedGridObject;
  private String towerName;

  public GameGrid(OrthographicCamera camera) {
    this();
    gameGridRenderer = new GameGridRenderer(this, camera);
  }

  public GameGrid() {
    setTouchEnabled(true);

    gridObjects = new TypeInstanceMap<GridObject>();
    positionCache = new GridPositionCache(this);

    gridSize = new GridPoint(8, 8);
    gridOrigin = new GridPoint();
    gridScale = 1f;

    updateWorldSize(true);
  }

  public void updateWorldSize(boolean copyGridPositions) {
    worldSize = new Vector2(gridSize.x * TowerConsts.GRID_UNIT_SIZE * gridScale, gridSize.y * TowerConsts.GRID_UNIT_SIZE * gridScale);
    worldBounds = new Rectangle(gridOrigin.x, gridOrigin.y, worldSize.x, worldSize.y);

    events().post(new GameGridResizeEvent(this, copyGridPositions));
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
  }

  public void setGridSize(float x, float y) {
    setGridSize((int) x, (int) y);
  }

  public GameGridRenderer getRenderer() {
    return gameGridRenderer;
  }

  public Vector2 getWorldSize() {
    return worldSize.cpy();
  }

  public GridPoint getGridSize() {
    return gridSize;
  }

  public boolean addObject(GridObject gridObject) {
    gridObjects.add(gridObject);
    gameGridRenderer.updateRenderOrder(Lists.newArrayList(gridObjects.getInstances()));

    int objectYPos = gridObject.getPosition().y;
    if (objectYPos > highestPoint) {
      highestPoint = objectYPos;
      if (highestPoint + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE > gridSize.y) {
        gridSize.y = highestPoint + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE;
        updateWorldSize(true);
      }
    }

    events().post(new GridObjectAddedEvent(gridObject));

    return true;
  }

  public LinkedList<GridObject> getObjects() {
    return gridObjects.getInstances();
  }

  public boolean canObjectBeAt(GridObject gridObject) {
    if (!gridObject.canBeAt()) {
      return false;
    }

    Rectangle boundsOfGridObjectToCheck = gridObject.getBounds();
    LinkedList<GridObject> instances = gridObjects.getInstances();
    for (int i = 0, instancesSize = instances.size(); i < instancesSize; i++) {
      GridObject child = instances.get(i);
      if (child != gridObject) {
        if (child.getBounds().contains(boundsOfGridObjectToCheck) && !child.canShareSpace(gridObject)) {
          return false;
        }
      }
    }

    return true;
  }

  public void removeObject(GridObject gridObject) {
    gridObjects.remove(gridObject);
    ArrayList<GridObject> arrayList = Lists.newArrayList(gridObjects.getInstances());
    arrayList.remove(gridObject);
    gameGridRenderer.updateRenderOrder(arrayList);

    events().post(new GridObjectRemovedEvent(gridObject));
  }

  public void update(float deltaTime) {
    super.update(deltaTime);

//    HACK: have to figure out a better way to clear out previously selected grid objects, until then...
    if (selectedGridObject != null && !Gdx.input.isTouched()) {
      selectedGridObject.touchUp();
      selectedGridObject = null;
    }

    LinkedList<GridObject> instances = gridObjects.getInstances();
    for (int i = 0, instancesSize = instances.size(); i < instancesSize; i++) {
      GridObject gridObject = instances.get(i);
      gridObject.update(deltaTime);
    }
  }

  public ArrayList<GridObject> getInstancesOf(Class aClass) {
    return gridObjects.setForType(aClass);
  }

  public List<GridObject> getInstancesOf(Class... classes) {
    List<GridObject> found = Lists.newArrayList();
    if (classes != null) {
      for (int i = 0, classesLength = classes.length; i < classesLength; i++) {
        Class otherClass = classes[i];
        found.addAll(getInstancesOf(otherClass));
      }
    }

    return found;
  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    GridPoint gridPointAtFinger = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = positionCache.getObjectsAt(gridPointAtFinger, SINGLE_POINT);
    for (GridObject gridObject : gridObjects) {
      if (gridObject.tap(gridPointAtFinger, count)) {
        return true;
      }
    }

    return false;
  }

  public GridPoint closestGridPoint(Vector2 worldPoint) {
    return closestGridPoint(worldPoint.x, worldPoint.y);
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    GridPoint gameGridPoint = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = positionCache.getObjectsAt(gameGridPoint, SINGLE_POINT);
    for (GridObject gridObject : gridObjects) {
      if (gridObject.touchDown(gameGridPoint, worldPoint, pointer)) {
        selectedGridObject = gridObject;
        System.out.println("Selected: " + gridObject);
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }


  @Override
  public boolean pan(Vector2 worldPoint, Vector2 deltaPoint) {
    if (selectedGridObject != null) {
      GridPoint gridPointAtFinger = closestGridPoint(worldPoint);
      GridPoint gridPointDelta = closestGridPoint(deltaPoint);
      if (selectedGridObject.pan(gridPointAtFinger, gridPointDelta)) {
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }

  public GridPoint closestGridPoint(float x, float y) {
    int gridX = (int) Math.floor((int) (x - gridOrigin.x) / TowerConsts.GRID_UNIT_SIZE);
    int gridY = (int) Math.floor((int) (y - gridOrigin.y) / TowerConsts.GRID_UNIT_SIZE);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new GridPoint(gridX, gridY);
  }

  public EventBus events() {
    return eventBus;
  }

  public void clearObjects() {
    gridObjects.clear();
    positionCache = new GridPositionCache(this);
  }

  public boolean isEmpty() {
    return gridObjects.isEmpty();
  }

  public GridPositionCache positionCache() {
    return positionCache;
  }

  public void setGridOrigin(GridPoint gridOrigin) {
    this.gridOrigin.set(gridOrigin);
  }

  public GridPoint getGridOrigin() {
    return gridOrigin;
  }

  public Rectangle getWorldBounds() {
    return worldBounds;
  }

  public float getGridScale() {
    return gridScale;
  }

  public void setGridScale(float gridScale) {
    this.gridScale = gridScale;
  }

  public float toWorldSpace(int gridSpaces) {
    return TowerConsts.GRID_UNIT_SIZE * gridSpaces * gridScale;
  }


  public String getTowerName() {
    return towerName;
  }

  public void setTowerName(String towerName) {
    this.towerName = towerName;
  }
}
