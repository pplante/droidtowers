package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GridObjectAddedEvent;
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
    gameGridRenderer = new GameGridRenderer(this);
    gridColor = Color.GREEN;
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);
    updateWorldSize();

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

    updateRenderOrder();
  }

  public void updateWorldSize() {
    worldSize = new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
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
        if (child.getBounds().intersects(boundsOfGridObjectToCheck) && !gridObject.canShareSpace(child)) {
          return false;
        }
      }
    }

    return true;
  }

  public List<GridObject> getObjectsAt(Bounds2d targetBounds, GridObject... objectsToIgnore) {
    List<GridObject> objectsFound = Lists.newArrayList();

    for (GridObject gridObject : objects) {
      if (gridObject.getBounds().overlaps(targetBounds)) {
        objectsFound.add(gridObject);
      }
    }

    if (objectsToIgnore != null) {
      objectsFound.removeAll(Lists.newArrayList(objectsToIgnore));
    }

    return objectsFound;
  }

  public Vector2 convertScreenPointToGridPoint(float x, float y) {
    float gridX = (float) Math.floor(x / unitSize.x);
    float gridY = (float) Math.floor(y / unitSize.y);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new Vector2(gridX, gridY);
  }

  public Vector2 clampPosition(Vector2 position, Vector2 size) {
    if (position.x < 0) {
      position.x = 0;
    } else if (position.x + size.x > gridSize.x) {
      position.x = gridSize.x - size.x;
    }

    if (position.y < 0) {
      position.y = 0;
    } else if (position.y + size.y > gridSize.y) {
      position.y = gridSize.y - size.y;
    }

    return position;
  }

  public void removeObject(GridObject gridObject) {
    objects.remove(gridObject);
    updateRenderOrder();
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
    Class<? extends EventObject> eventClass = event.getClass();
    for (EventListener subscriber : eventSubscribers) {
      subscriber.receiveEvent(eventClass.cast(event));
    }
  }
}
