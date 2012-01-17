package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectRemovedEvent;
import com.unhappyrobot.math.Bounds2d;

import java.util.*;


public class GameGrid {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridSize;

  private HashSet<GridObject> objects;
  private List<GridObject> objectsRenderOrder;
  private Vector2 worldSize;
  private final Function<GridObject, Integer> objectRenderSortFunction;
  private final GameGridRenderer gameGridRenderer;
  private final Map<Class, Set<GridObject>> gridObjectsByType;
  private final HashSet<EventListener> eventSubscribers;

  public GameGrid() {
    gridObjectsByType = new HashMap<Class, Set<GridObject>>();
    eventSubscribers = new HashSet<EventListener>();
    objects = new HashSet<GridObject>(25);
    objectRenderSortFunction = new Function<GridObject, Integer>() {
      public Integer apply(@Nullable GridObject gridObject) {
        if (gridObject != null) {
          if (gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
            return gridObject.getGridObjectType().getZIndex();
          } else {
            return Integer.MAX_VALUE;
          }
        }
        return 0;
      }
    };

    gameGridRenderer = new GameGridRenderer(this);
    gridColor = Color.GREEN;
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);
    updateWorldSize();
    updateRenderOrder();
  }

  public void updateWorldSize() {
    worldSize = new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
    broadcastEvent(new GameGridResizeEvent(this));
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
    updateRenderOrder();

    Set<GridObject> gridObjectHashSet;
    if (!gridObjectsByType.containsKey(gridObject.getClass())) {
      gridObjectHashSet = new HashSet<GridObject>();
      gridObjectsByType.put(gridObject.getClass(), gridObjectHashSet);
    } else {
      gridObjectHashSet = gridObjectsByType.get(gridObject.getClass());
    }

    gridObjectHashSet.add(gridObject);

    broadcastEvent(new GridObjectAddedEvent(gridObject));

    return true;
  }

  public void updateRenderOrder() {
    objectsRenderOrder = Ordering.natural().onResultOf(objectRenderSortFunction).sortedCopy(objects);
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
    updateRenderOrder();

    broadcastEvent(new GridObjectRemovedEvent(gridObject));
  }

  public List<GridObject> getObjectsInRenderOrder() {
    return objectsRenderOrder;
  }

  public void update(float deltaTime) {
    for (GridObject gridObject : objects) {
      gridObject.update(deltaTime);
    }
  }

  public Set<GridObject> getInstancesOf(Class aClass) {
    return gridObjectsByType.get(aClass);
  }

  public Set<GridObject> getInstancesOf(Class[] classes) {
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

  public void addEventListener(EventListener newListener) {
    eventSubscribers.add(newListener);
  }

  public void broadcastEvent(EventObject event) {
    if (eventSubscribers != null) {
      Class<? extends EventObject> eventClass = event.getClass();
      for (EventListener subscriber : eventSubscribers) {
        subscriber.receiveEvent(eventClass.cast(event));
      }
    }
  }
}
