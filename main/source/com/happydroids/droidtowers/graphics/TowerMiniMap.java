/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.events.WeatherState;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.GridPoint;

import static java.lang.Math.*;

public class TowerMiniMap extends Table {
  private static final String TAG = TowerMiniMap.class.getSimpleName();

  private final GameGrid gameGrid;
  private boolean mapIsDirty;
  private Pixmap pixmap;

  public TowerMiniMap(GameGrid gameGrid) {
    super();
    this.gameGrid = gameGrid;

//    Pixmap backgroundPixmap = new Pixmap(2, 2, Pixmap.Format.RGB565);
//    backgroundPixmap.setColor(WeatherState.SUNNY.color);
//    backgroundPixmap.fill();
//
//    setBackground(new NinePatch(new Texture(backgroundPixmap)));
  }

  public static Pixmap redrawMiniMap(GameGrid gameGrid, boolean useCustomScale, float customScale) {
    Gdx.app.debug(TAG, "Redrawing minimap!");
    GridPoint gridSize = gameGrid.getGridSize();

    float maxSize = customScale;
    if (!useCustomScale) {
      maxSize = min(gridSize.x, gridSize.y) / max(gridSize.x, gridSize.y);
    }

    float pixmapWidth = MathUtils.nextPowerOfTwo((int) (gridSize.x * maxSize));
    float pixmapHeight = MathUtils.nextPowerOfTwo((int) (gridSize.y * maxSize));
    double objWidth, objHeight, xPos, yPos;

    Pixmap pixmap = new Pixmap((int) pixmapWidth, (int) pixmapHeight, Pixmap.Format.RGB565);
    pixmap.setColor(WeatherState.SUNNY.skyColor);
    pixmap.fill();

    double landHeight = floor((TowerConsts.LOBBY_FLOOR - 1) * maxSize);
    double landY = pixmapHeight - landHeight;

    pixmap.setColor(Color.ORANGE);
    pixmap.fillRectangle(0, (int) landY, (int) pixmapWidth, (int) landHeight);

    GridPosition[][] positions = gameGrid.positionCache().getPositions();
    for (GridPosition[] row : positions) {
      for (GridPosition position : row) {
        if (position.size() == 0) {
          continue;
        }

        pixmap.setColor(Color.GRAY);

        if (!position.connectedToTransit) {
          pixmap.setColor(Color.RED);
        } else if (position.elevator != null) {
          pixmap.setColor(Color.DARK_GRAY);
        }

        if (useCustomScale && customScale != 1f) {
          pixmap.fillRectangle(round(position.x * maxSize), round(pixmapHeight - (position.y * maxSize)), (int) customScale, (int) customScale);
        } else {
          pixmap.drawPixel(round(position.x * maxSize), round(pixmapHeight - (position.y * maxSize)));
        }
      }
    }

    return pixmap;
  }
}
