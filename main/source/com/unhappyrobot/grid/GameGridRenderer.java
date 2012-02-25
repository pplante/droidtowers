package com.unhappyrobot.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.GridObjectPlacementState;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectChangedEvent;
import com.unhappyrobot.events.GridObjectRemovedEvent;
import com.unhappyrobot.graphics.Overlays;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.math.GridPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameGridRenderer extends GameLayer {
  private GameGrid gameGrid;
  private boolean shouldRenderGridLines;
  private final ShapeRenderer shapeRenderer;
  private Function<GridObject, Color> employmentLevelOverlayFunc;
  private Function<GridObject, Color> populationLevelOverlayFunc;
  private Function<GridObject, Color> desirabilityLevelOverlayFunc;
  private HashSet<Overlays> activeOverlays;
  private Map<Overlays, Function<GridObject, Float>> overlayFunctions;
  private Function<GridObject, Integer> objectRenderSortFunction;
  private List<GridObject> objectsRenderOrder;
  private List<TransitLine> transitLines;
  private boolean shouldRenderTransitLines;

  public GameGridRenderer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    GameEvents.register(this);

    shouldRenderGridLines = true;
    shapeRenderer = new ShapeRenderer();

    transitLines = Lists.newArrayList();

    activeOverlays = new HashSet<Overlays>();

    objectsRenderOrder = Lists.newArrayList();
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

    makeOverlayFunctions();
  }

  @Override
  public void render(SpriteBatch spriteBatch, Camera camera) {
    shapeRenderer.setProjectionMatrix(TowerGame.getCamera().combined);

    if (shouldRenderGridLines) {
      renderGridLines();
    }

    renderGridObjects(spriteBatch);

    if (activeOverlays.size() > 0) {
      Gdx.gl.glEnable(GL10.GL_BLEND);

      for (Overlays overlay : activeOverlays) {
        if (overlay.equals(Overlays.NOISE_LEVEL)) {
          renderNoiseLevelOverlay();
        } else {
          renderGenericOverlay(overlay);
        }
      }
    }

    if (shouldRenderTransitLines && transitLines.size() > 0) {
      Gdx.gl.glEnable(GL10.GL_BLEND);
      for (TransitLine transitLine : transitLines) {
        transitLine.render(shapeRenderer);
      }
    }
  }

  private void makeOverlayFunctions() {
    overlayFunctions = new HashMap<Overlays, Function<GridObject, Float>>();

    overlayFunctions.put(Overlays.EMPLOYMENT_LEVEL, Overlays.EMPLOYMENT_LEVEL.getMethod());
    overlayFunctions.put(Overlays.POPULATION_LEVEL, Overlays.POPULATION_LEVEL.getMethod());
    overlayFunctions.put(Overlays.DESIRABILITY_LEVEL, Overlays.DESIRABILITY_LEVEL.getMethod());
  }

  private void renderGenericOverlay(Overlays overlay) {
    Function<GridObject, Float> function = overlayFunctions.get(overlay);
    Color baseColor = overlay.getColor(1f);

    shapeRenderer.begin(ShapeType.FilledRectangle);

    for (GridObject gridObject : gameGrid.getObjects()) {
      Float returnValue = function.apply(gridObject);
      if (returnValue != null) {
        baseColor.a = returnValue;
        shapeRenderer.setColor(baseColor);
        shapeRenderer.filledRect(gridObject.getPosition().getWorldX(gameGrid), gridObject.getPosition().getWorldY(gameGrid), gridObject.getSize().getWorldX(gameGrid), gridObject.getSize().getWorldY(gameGrid));
      }
    }

    shapeRenderer.end();
  }

  private void renderNoiseLevelOverlay() {
    shapeRenderer.begin(ShapeType.FilledRectangle);
    for (GridObject gridObject : gameGrid.getObjects()) {
      float noiseLevel = gridObject.getNoiseLevel();

      if (noiseLevel > 0) {
        GridPoint position = gridObject.getPosition().cpy();
        GridPoint size = gridObject.getSize().cpy();
        float colorStep = noiseLevel / 2f;

        for (int i = 0; i < 2; i++) {
          position.sub(i, i);
          size.add(i * 2, i * 2);

          shapeRenderer.filledRect(position.getWorldX(gameGrid), position.getWorldY(gameGrid), size.getWorldX(gameGrid), size.getWorldY(gameGrid));
          shapeRenderer.setColor(Overlays.NOISE_LEVEL.getColor(noiseLevel));

          noiseLevel -= colorStep;
        }

      }
    }
    shapeRenderer.end();
  }

  private void renderGridObjects(SpriteBatch spriteBatch) {
    spriteBatch.begin();

    for (GridObject child : objectsRenderOrder) {
      child.render(spriteBatch);
    }

    spriteBatch.end();
  }

  private void renderGridLines() {
    Gdx.gl.glEnable(GL10.GL_BLEND);

    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(gameGrid.gridColor);

    for (int i = 0; i <= gameGrid.gridSize.x; i++) {
      shapeRenderer.line(i * gameGrid.unitSize.x, 0, i * gameGrid.unitSize.x, gameGrid.gridSize.y * gameGrid.unitSize.y);
    }

    for (int i = 0; i <= gameGrid.gridSize.y; i++) {
      shapeRenderer.line(0, i * gameGrid.unitSize.y, gameGrid.gridSize.x * gameGrid.unitSize.x, i * gameGrid.unitSize.y);
    }

    shapeRenderer.end();
  }

  public void toggleGridLines() {
    shouldRenderGridLines = !shouldRenderGridLines;
  }

  public void addActiveOverlay(Overlays overlay) {
    activeOverlays.add(overlay);
  }

  public void removeActiveOverlay(Overlays overlay) {
    activeOverlays.remove(overlay);
  }

  public void clearOverlays() {
    activeOverlays.clear();
  }

  private void updateRenderOrder() {
    objectsRenderOrder = null;
    objectsRenderOrder = Ordering.natural().onResultOf(objectRenderSortFunction).sortedCopy(gameGrid.getObjects());
  }

  @Subscribe
  public void handleEvent(GridObjectAddedEvent event) {
    if (event.gridObject == null) {
      return;
    }

    updateRenderOrder();
  }

  @Subscribe
  public void handleEvent(GridObjectChangedEvent event) {
    if (event.gridObject == null || !event.nameOfParamChanged.equals("placementState")) {
      return;
    }

    updateRenderOrder();
  }

  @Subscribe
  public void handleEvent(GridObjectRemovedEvent event) {
    if (event.gridObject == null) {
      return;
    }

    objectsRenderOrder.remove(event.gridObject);
  }

  public void removeTransitLine(TransitLine transitLine) {
    transitLines.remove(transitLine);
  }

  public void addTransitLine(TransitLine transitLine) {
    transitLines.add(transitLine);
  }

  public void toggleTransitLines() {
    shouldRenderTransitLines = !shouldRenderTransitLines;
  }
}
