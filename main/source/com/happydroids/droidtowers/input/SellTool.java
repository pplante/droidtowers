/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class SellTool extends ToolBase {
  public SellTool(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
    super(camera, gameLayers, gameGrid);
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector3 worldPoint = camera.getPickRay(x, y).getEndPoint(1);
    GridPoint gridPointAtFinger = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);

    Set<GridObject> gridObjects = gameGrid.positionCache().getObjectsAt(gridPointAtFinger, TowerConsts.SINGLE_POINT);

    if (gridObjects != null) {
      List<GridObject> zIndexSorted = Ordering.natural().reverse().onResultOf(new Function<GridObject, Integer>() {
        public Integer apply(@Nullable GridObject o) {
          return o.getGridObjectType().getZIndex();
        }
      }).sortedCopy(gridObjects);

      if (zIndexSorted != null && zIndexSorted.size() > 0) {
        final GridObject objectToSell = zIndexSorted.get(0);
        final int sellPrice = (int) (objectToSell.getGridObjectType().getCoins() * 0.5);

        new SellGridObjectConfirmationDialog(gameGrid, objectToSell).show();

        return true;
      }
    }

    return false;
  }

}
