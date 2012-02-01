package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectRemovedEvent;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.math.GridPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GameGrid extends GameLayer {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridSize;

  private HashSet<GridObject> objects;
  private Vector2 worldSize;
  private final GameGridRenderer gameGridRenderer;
  private final Map<Class, Set<GridObject>> gridObjectsByType;
  private GridObject selectedGridObject;
  private GridObject transitGridObjectA;
  private GridObject transitGridObjectB;

  public GameGrid() {
    setTouchEnabled(true);

    gridObjectsByType = new HashMap<Class, Set<GridObject>>();
    objects = new HashSet<GridObject>(25);

    gameGridRenderer = new GameGridRenderer(this);
    gridColor = Color.GREEN;
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);
    updateWorldSize();
  }

  public void updateWorldSize() {
    worldSize = new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
    GameEvents.post(new GameGridResizeEvent(this));
  }

  public void setUnitSize(int width, int height) {
    unitSize.set(width, height);
    updateWorldSize();
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
    updateWorldSize();
  }

  public void setGridColor(float r, float g, float b, float a) {
    gridColor.set(r, g, b, a);
  }

  public GameGridRenderer getRenderer() {
    return gameGridRenderer;
  }

  public Vector2 getWorldSize() {
    return worldSize.cpy();
  }

  public boolean addObject(GridObject gridObject) {
    objects.add(gridObject);

    Set<GridObject> gridObjectHashSet;
    if (!gridObjectsByType.containsKey(gridObject.getClass())) {
      gridObjectHashSet = new HashSet<GridObject>();
      gridObjectsByType.put(gridObject.getClass(), gridObjectHashSet);
    } else {
      gridObjectHashSet = gridObjectsByType.get(gridObject.getClass());
    }

    gridObjectHashSet.add(gridObject);

    GameEvents.post(new GridObjectAddedEvent(gridObject));

    return true;
  }

  public Set<GridObject> getObjects() {
    return objects;
  }

  public boolean canObjectBeAt(GridObject gridObject) {
    if (!gridObject.canBeAt()) {
      return false;
    }

    Bounds2d boundsOfGridObjectToCheck = gridObject.getBounds();
    for (GridObject child : objects) {
      if (child != gridObject) {
        if (child.getBounds().intersects(boundsOfGridObjectToCheck) && !child.canShareSpace(gridObject)) {
          return false;
        }
      }
    }

    return true;
  }

  public void removeObject(GridObject gridObject) {
    objects.remove(gridObject);

    GameEvents.post(new GridObjectRemovedEvent(gridObject));
  }

  public void update(float deltaTime) {
    for (GridObject gridObject : objects) {
      gridObject.update(deltaTime);
    }
  }

  public Set<GridObject> getInstancesOf(Class aClass) {
    return gridObjectsByType.get(aClass);
  }

  public Set<GridObject> getInstancesOf(Class... classes) {
    Set<GridObject> found = new HashSet<GridObject>();
    if (classes != null) {
      for (Class otherClass : classes) {
        if (gridObjectsByType.containsKey(otherClass)) {
          found.addAll(gridObjectsByType.get(otherClass));
        }
      }
    }

    return found;
  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    GridPoint gridPointAtFinger = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = GridPositionCache.instance().getObjectsAt(gridPointAtFinger, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.tap(gridPointAtFinger, count)) {
        return true;
      }
    }

    return false;
  }

  private GridPoint closestGridPoint(Vector2 worldPoint) {
    return closestGridPoint(worldPoint.x, worldPoint.y);
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    GridPoint gameGridPoint = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = GridPositionCache.instance().getObjectsAt(gameGridPoint, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.touchDown(gameGridPoint)) {
        selectedGridObject = gridObject;
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
    float gridX = (float) Math.floor((int) x / unitSize.x);
    float gridY = (float) Math.floor((int) y / unitSize.y);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new GridPoint(gridX, gridY);
  }
}
