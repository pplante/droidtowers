package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

import java.util.Set;

public class PickerTool extends ToolBase {
  private GridObject selectedGridObject;

  public PickerTool(OrthographicCamera camera, GameGrid gameGrid) {
    super(camera, gameGrid);
  }

  @Override
  public boolean tap(int x, int y, int count) {
    GridPoint gridPointAtFinger = gridPointAtFinger();

    Set<GridObject> gridObjects = GridPositionCache.instance().getObjectsAt(gridPointAtFinger, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.tap(gridPointAtFinger, count)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer) {
    GridPoint gameGridPoint = screenToGameGridPoint(x, y);

    Set<GridObject> gridObjects = GridPositionCache.instance().getObjectsAt(gameGridPoint, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.touchDown(gameGridPoint)) {
        selectedGridObject = gridObject;
        System.out.println("selectedGridObject = " + selectedGridObject);
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }

  @Override
  public boolean pan(int x, int y, int deltaX, int deltaY) {

    if (selectedGridObject != null) {
      GridPoint gridPointAtFinger = gridPointAtFinger();
      GridPoint gridPointDelta = screenToGameGridPoint(x + -deltaX, y + deltaY);
      if (selectedGridObject.pan(gridPointAtFinger, gridPointDelta)) {
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }

  private Vector2 screenToGameGridPoint(float x, float y) {
    return screenToGameGridPoint((int) x, (int) y);
  }
}
