package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.types.Elevator;

import java.util.List;

public class PickerTool extends ToolBase {
  public PickerTool(OrthographicCamera camera, GameGrid gameGrid) {
    super(camera, gameGrid);
  }

  @Override
  public boolean tap(int x, int y, int count) {
    Vector2 gridPointAtFinger = findGameGridPointAtFinger();

    List<GridObject> gridObjects = gameGrid.getObjectsAt(new Bounds2d(gridPointAtFinger, new Vector2(1, 1)));

    if (gridObjects.size() > 0) {
      for (GridObject gridObject : gridObjects) {
        if (gridObject instanceof Elevator) {
          gridObject.size.add(0, 1);
        }
      }
    }

    return false;
  }
}
