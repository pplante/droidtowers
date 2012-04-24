/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPositionCache;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.math.GridPoint;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

import static com.happydroids.droidtowers.gui.ResponseType.NEGATIVE;
import static com.happydroids.droidtowers.gui.ResponseType.POSITIVE;

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
      List<GridObject> zIndexSorted = Ordering.natural().reverse().onResultOf(new Function<GridObject, Integer>() {
        public Integer apply(@Nullable GridObject o) {
          return o.getGridObjectType().getZIndex();
        }
      }).sortedCopy(gridObjects);

      if (zIndexSorted != null && zIndexSorted.size() > 0) {
        final GridObject objectToSell = zIndexSorted.get(0);
        final int sellPrice = (int) (objectToSell.getGridObjectType().getCoins() * 0.5);
        new Dialog()
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to sell this " + objectToSell.getGridObjectType().getName() + "?\n\nCurrent market price is: $" + NumberFormat.getInstance().format(sellPrice))
                .addButton(POSITIVE, "Yes", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                    Gdx.input.vibrate(100);
                    gameGrid.removeObject(objectToSell);
                    Player.instance().addCurrency(sellPrice);
                  }
                })
                .addButton(NEGATIVE, "No", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                  }
                })
                .show();

        return true;
      }
    }

    return false;
  }
}
