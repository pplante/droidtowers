package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.math.Bounds2d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGrid {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridSize;

  private long lastEarnoutTime;

  private HashSet<GridObject> objects;
  private List<GridObject> objectsRenderOrder;
  private Vector2 worldSize;
  private final Function<GridObject, Integer> objectRenderSortFunction;
  private static final long EARN_OUT_INTERVAL_MILLIS = 5000;
  private final GameGridRenderer gameGridRenderer;

  public GameGrid() {
    gameGridRenderer = new GameGridRenderer(this);
    gridColor = Color.GREEN;
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);
    updateWorldSize();

    lastEarnoutTime = System.currentTimeMillis();

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
    Player player = Player.getInstance();
    if ((lastEarnoutTime + EARN_OUT_INTERVAL_MILLIS) < System.currentTimeMillis()) {
      lastEarnoutTime = System.currentTimeMillis();

      int coinsEarned = 0;
      int goldEarned = 0;
      for (GridObject object : objects) {
        coinsEarned += object.getCoinsEarned();
        goldEarned += object.getGoldEarned();
      }
      System.out.println(String.format("Player earned: %d coins and %d gold", coinsEarned, goldEarned));
      player.addCurrency(coinsEarned, goldEarned);
    }

    int currentPopulation = 0;
    for (GridObject gridObject : objects) {
      gridObject.update(deltaTime);

      if (gridObject instanceof Room) {
        currentPopulation += ((Room) gridObject).getCurrentPopulation();
      }
    }

    player.setPopulation(currentPopulation);
  }
}
