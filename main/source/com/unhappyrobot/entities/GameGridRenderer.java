package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.Overlays;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.types.RoomType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameGridRenderer extends GameLayer {
  private ImmediateModeRenderer10 gl;
  private GameGrid gameGrid;
  private boolean shouldRenderGridLines;
  private final ShapeRenderer shapeRenderer;
  private Function<GridObject, Color> employmentLevelOverlayFunc;
  private Function<GridObject, Color> populationLevelOverlayFunc;
  private Function<GridObject, Color> desirabilityLevelOverlayFunc;
  private HashSet<Overlays> activeOverlays;
  private Map<Overlays, Function<GridObject, Color>> overlayFunctions;

  public GameGridRenderer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    shouldRenderGridLines = true;
    gl = new ImmediateModeRenderer10();
    shapeRenderer = new ShapeRenderer();

    activeOverlays = new HashSet<Overlays>();

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
          renderGenericOverlay(overlayFunctions.get(overlay));
        }
      }
    }
  }

  private void makeOverlayFunctions() {
    overlayFunctions = new HashMap<Overlays, Function<GridObject, Color>>();

    overlayFunctions.put(Overlays.EMPLOYMENT_LEVEL, new Function<GridObject, Color>() {
      public Color apply(@Nullable GridObject gridObject) {
        if (gridObject instanceof CommercialSpace) {
          float jobsProvided = ((CommercialType) gridObject.getGridObjectType()).getJobsProvided();
          if (jobsProvided > 0f) {
            return new Color(0, 1, 0, ((CommercialSpace) gridObject).getJobsFilled() / jobsProvided);
          }
        }

        return null;
      }
    });

    overlayFunctions.put(Overlays.POPULATION_LEVEL, new Function<GridObject, Color>() {
      public Color apply(@Nullable GridObject gridObject) {
        if (gridObject instanceof Room) {
          float populationMax = ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
          if (populationMax > 0f) {
            return new Color(0, 0, 1, ((Room) gridObject).getCurrentResidency() / populationMax);
          }
        }

        return null;
      }
    });

    overlayFunctions.put(Overlays.DESIRABLITY_LEVEL, new Function<GridObject, Color>() {
      public Color apply(@Nullable GridObject gridObject) {
        if (gridObject instanceof Room && !(gridObject instanceof CommercialSpace)) {
          float desirability = ((Room) gridObject).getDesirability();
          return new Color(0, 0, 1, desirability);
        }

        return null;
      }
    });
  }

  private void renderGenericOverlay(Function<GridObject, Color> function) {
    shapeRenderer.begin(ShapeType.FilledRectangle);

    for (GridObject gridObject : gameGrid.getObjects()) {
      Color blockColor = function.apply(gridObject);
      if (blockColor != null) {
        shapeRenderer.setColor(blockColor);
        shapeRenderer.filledRect(gridObject.position.getWorldX(gameGrid), gridObject.position.getWorldY(gameGrid), gridObject.size.getWorldX(gameGrid), gridObject.size.getWorldY(gameGrid));
      }
    }

    shapeRenderer.end();
  }

  private void renderNoiseLevelOverlay() {
    shapeRenderer.begin(ShapeType.FilledRectangle);
    for (GridObject gridObject : gameGrid.getObjects()) {
      float noiseLevel = gridObject.getNoiseLevel();

      if (noiseLevel > 0) {
        GridPoint position = gridObject.position.cpy();
        GridPoint size = gridObject.size.cpy();
        float colorStep = noiseLevel / 2f;

        for (int i = 0; i < 2; i++) {
          position.sub(i, i);
          size.add(i * 2, i * 2);

          shapeRenderer.filledRect(position.getWorldX(gameGrid), position.getWorldY(gameGrid), size.getWorldX(gameGrid), size.getWorldY(gameGrid));
          shapeRenderer.setColor(1, 0, 0, noiseLevel);

          noiseLevel -= colorStep;
        }

      }
    }
    shapeRenderer.end();
  }

  private void renderGridObjects(SpriteBatch spriteBatch) {
    spriteBatch.begin();
    for (GridObject child : gameGrid.getObjectsInRenderOrder()) {
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
}
