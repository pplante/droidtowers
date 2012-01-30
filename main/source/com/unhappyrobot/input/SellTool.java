package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

import java.util.List;
import java.util.Set;

public class SellTool extends ToolBase {
  private GameGrid gameGrid;

  public SellTool(OrthographicCamera camera, List<GameLayer> gameLayers) {
    super(camera, gameLayers);

    gameGrid = getGameGrid();
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector3 worldPoint = camera.getPickRay(x, y).getEndPoint(1);
    GridPoint gridPointAtFinger = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);

    Set<GridObject> gridObjects = GridPositionCache.instance().getObjectsAt(gridPointAtFinger, new Vector2(1, 1));

    if (gridObjects != null) {
      for (GridObject gridObject : gridObjects) {
        gameGrid.removeObject(gridObject);
      }

      return true;
    }

    return false;
  }
}
