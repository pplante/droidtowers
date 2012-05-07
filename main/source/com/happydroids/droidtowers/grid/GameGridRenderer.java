/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.GridObjectAddedEvent;
import com.happydroids.droidtowers.events.GridObjectChangedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.graphics.TransitLine;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;
import static com.happydroids.droidtowers.graphics.Overlays.POPULATION_LEVEL;

public class GameGridRenderer extends GameLayer {
  protected GameGrid gameGrid;
  protected final OrthographicCamera camera;
  protected boolean shouldRenderGridLines;
  protected final ShapeRenderer shapeRenderer;
  private Function<GridObject, Color> employmentLevelOverlayFunc;
  private Function<GridObject, Color> populationLevelOverlayFunc;
  private Function<GridObject, Color> desirabilityLevelOverlayFunc;
  private HashSet<Overlays> activeOverlays;
  private Map<Overlays, Function<GridObject, Float>> overlayFunctions;
  private Function<GridObject, Integer> objectRenderSortFunction;
  private List<GridObject> objectsRenderOrder;
  private List<TransitLine> transitLines;
  private boolean shouldRenderTransitLines;
  protected Color renderTintColor;

  public GameGridRenderer(GameGrid gameGrid, OrthographicCamera camera) {
    this.gameGrid = gameGrid;
    this.camera = camera;

    gameGrid.events().register(this);

    renderTintColor = Color.WHITE;
    shouldRenderGridLines = false;
    shapeRenderer = new ShapeRenderer();

    transitLines = Lists.newArrayList();

    activeOverlays = Sets.newHashSet();

    objectsRenderOrder = Lists.newArrayList();
    objectRenderSortFunction = new Function<GridObject, Integer>() {
      public Integer apply(@Nullable GridObject gridObject) {
        if (gridObject != null) {
          if (gridObject.isPlaced()) {
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
  public void render(SpriteBatch spriteBatch) {
    shapeRenderer.setProjectionMatrix(camera.combined);
    if (shouldRenderGridLines) {
      renderGridLines();
    }

    renderGridObjects(spriteBatch);

    if (activeOverlays.size() > 0) {
      for (Overlays overlay : activeOverlays) {
        if (overlay.equals(Overlays.NOISE_LEVEL)) {
          renderNoiseLevelOverlay();
        } else {
          renderGenericOverlay(overlay);
        }
      }
    }

    if (shouldRenderTransitLines && transitLines.size() > 0) {
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
        shapeRenderer.filledRect(gridObject.getPosition().getWorldX(), gridObject.getPosition().getWorldY(), gridObject.getSize().getWorldX(), gridObject.getSize().getWorldY());
      }
    }

    shapeRenderer.end();
  }

  private void renderNoiseLevelOverlay() {
    shapeRenderer.begin(ShapeType.FilledRectangle);

    for (int x = 0; x < gameGrid.gridSize.x; x++) {
      for (int y = 0; y < gameGrid.gridSize.y; y++) {
        GridPosition position = gameGrid.positionCache().getPosition(x, y);
        if (position.getNoiseLevel() > 0.01f) {
          shapeRenderer.filledRect(x * GRID_UNIT_SIZE, (y - 1) * GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);
          shapeRenderer.setColor(Overlays.NOISE_LEVEL.getColor(position.getNoiseLevel()));
        } else {
          shapeRenderer.setColor(Color.CLEAR);
        }
      }
    }

    shapeRenderer.end();
  }

  private void renderGridObjects(SpriteBatch spriteBatch) {
    spriteBatch.begin();

    for (GridObject child : objectsRenderOrder) {
      child.render(spriteBatch, renderTintColor);
    }

    spriteBatch.end();
  }

  private void renderGridLines() {
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.1f);

    for (int i = 0; i <= gameGrid.getGridSize().x; i++) {
      shapeRenderer.line(i * GRID_UNIT_SIZE, 0, i * GRID_UNIT_SIZE, gameGrid.getGridSize().y * GRID_UNIT_SIZE);
    }

    for (int i = 0; i <= gameGrid.getGridSize().y; i++) {
      shapeRenderer.line(0, i * GRID_UNIT_SIZE, gameGrid.getGridSize().x * GRID_UNIT_SIZE, i * GRID_UNIT_SIZE);
    }

    shapeRenderer.end();
  }

  public void toggleGridLines() {
    shouldRenderGridLines = !shouldRenderGridLines;
  }

  public void addActiveOverlay(Overlays overlay) {
    activeOverlays.add(overlay);

    if (overlay.equals(POPULATION_LEVEL)) {
      TutorialEngine.instance().moveToStepWhenReady("tutorial-turn-off-population-overlay");
    }
  }

  public void removeActiveOverlay(Overlays overlay) {
    activeOverlays.remove(overlay);

    if (overlay.equals(POPULATION_LEVEL)) {
      TutorialEngine.instance().moveToStepWhenReady("tutorial-finished");
    }
  }

  public void clearOverlays() {
    if (activeOverlays.contains(POPULATION_LEVEL)) {
      TutorialEngine.instance().moveToStepWhenReady("tutorial-finished");
    }
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
    if (event.gridObject == null || !event.nameOfParamChanged.equals("isPlaced")) {
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


  public void setRenderTintColor(Color renderTintColor) {
    this.renderTintColor = renderTintColor;
  }

  public Color getRenderTintColor() {
    return renderTintColor;
  }
}
