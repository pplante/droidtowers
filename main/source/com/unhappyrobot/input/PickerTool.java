package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.Bounds2d;

import java.util.List;

public class PickerTool extends ToolBase {
  private GridObject selectedGridObject;

  public PickerTool(OrthographicCamera camera, GameGrid gameGrid) {
    super(camera, gameGrid);
  }

  @Override
  public boolean tap(int x, int y, int count) {
    Vector2 gridPointAtFinger = gridPointAtFinger();

    List<GridObject> gridObjects = gameGrid.getObjectsAt(new Bounds2d(gridPointAtFinger, new Vector2(1, 1)));

    for (GridObject gridObject : gridObjects) {
      if (gridObject.tap(gridPointAtFinger, count)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer) {
    Vector2 gameGridPoint = screenToGameGridPoint(x, y);
    List<GridObject> gridObjects = gameGrid.getObjectsAt(new Bounds2d(gameGridPoint, new Vector2(1, 1)));

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
  public boolean pan(int x, int y, int deltaX, int deltaY) {

    if (selectedGridObject != null) {
      Vector2 gridPointAtFinger = gridPointAtFinger();
      Vector2 gridPointDelta = screenToGameGridPoint(x + -deltaX, y + deltaY);
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
