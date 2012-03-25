package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.events.WeatherState;
import com.unhappyrobot.gamestate.actions.GameGridTransportCalculationComplete;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;

import static java.lang.Math.*;

public class TowerMiniMap extends Table {
  private static final String TAG = TowerMiniMap.class.getSimpleName();

  private final GameGrid gameGrid;
  private boolean mapIsDirty;
  private Pixmap pixmap;

  public TowerMiniMap(GameGrid gameGrid) {
    super();
    this.gameGrid = gameGrid;
    gameGrid.events().register(this);

    Pixmap backgroundPixmap = new Pixmap(2, 2, Pixmap.Format.RGB565);
    backgroundPixmap.setColor(WeatherState.SUNNY.color);
    backgroundPixmap.fill();

    setBackground(new NinePatch(new Texture(backgroundPixmap)));
  }

  public Pixmap redrawMiniMap(boolean useCustomScale, float customScale) {
    Gdx.app.debug(TAG, "Redrawing minimap!");

    float maxSize = customScale;
    if (!useCustomScale) {
      maxSize = min(gameGrid.getGridSize().x, gameGrid.getGridSize().y) / max(gameGrid.getGridSize().x, gameGrid.getGridSize().y);
    }

    float pixmapWidth = gameGrid.getGridSize().x * maxSize;
    float pixmapHeight = gameGrid.getGridSize().y * maxSize;
    float pixmapRatio = min(pixmapWidth, pixmapHeight) / max(pixmapWidth, pixmapHeight);
    double objWidth, objHeight, xPos, yPos;

    Pixmap pixmap = new Pixmap((int) pixmapWidth, (int) pixmapHeight, Pixmap.Format.RGB565);
    pixmap.setColor(WeatherState.SUNNY.color);
    pixmap.fill();

    double landHeight = floor((TowerConsts.LOBBY_FLOOR - 1) * maxSize);
    double landY = pixmapHeight - landHeight;

    pixmap.setColor(Color.ORANGE);
    pixmap.fillRectangle(0, (int) landY, (int) pixmapWidth, (int) landHeight);

    GridPosition[][] positions = GridPositionCache.instance().getPositions();
    for (GridPosition[] row : positions) {
      for (GridPosition position : row) {
        if (position.size() == 0) continue;

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

  @Subscribe
  public void GameGrid_onGameGridTransportCalculationComplete(GameGridTransportCalculationComplete event) {
    Gdx.app.debug(TAG, "Requesting redraw because the game grid recalculated transport.");
    if (pixmap != null) {
      pixmap.dispose();
    }

    pixmap = redrawMiniMap(false, 1f);

    clear();
    Image image = new Image(new Texture(pixmap), Scaling.fit, Align.CENTER | Align.BOTTOM);
    add(image).size(150, 150);
    pack();
  }
}
