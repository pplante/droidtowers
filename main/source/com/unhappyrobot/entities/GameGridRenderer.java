package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.math.GridPoint;

public class GameGridRenderer extends GameLayer {
  private ImmediateModeRenderer10 gl;
  private GameGrid gameGrid;
  private boolean shouldRenderGridLines;
  private final ShapeRenderer shapeRenderer;

  public GameGridRenderer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    shouldRenderGridLines = true;
    gl = new ImmediateModeRenderer10();
    shapeRenderer = new ShapeRenderer();
  }

  @Override
  public void render(SpriteBatch spriteBatch, Camera camera) {
    if (shouldRenderGridLines) {
      renderGridLines();
    }

    renderGridObjects(spriteBatch);

    Gdx.gl.glEnable(GL10.GL_BLEND);
    shapeRenderer.setProjectionMatrix(TowerGame.getCamera().combined);

    for (GridObject gridObject : gameGrid.getObjects()) {
      float noiseLevel = gridObject.getGridObjectType().getNoiseLevel();

      if (noiseLevel > 0) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        Color color = new Color(Color.RED);
        color.a = noiseLevel;
        shapeRenderer.setColor(color);
        GridPoint position = gridObject.position.cpy();
        GridPoint size = gridObject.size.cpy();
        for (int i = 0; i < 2; i++) {
          position.sub(i, i);
          size.add(i * 2, i * 2);
          shapeRenderer.filledRect(position.getWorldX(), position.getWorldY(), size.getWorldX(), size.getWorldY());
          color.a -= noiseLevel / 2f;
          shapeRenderer.setColor(color);
        }
        shapeRenderer.end();
      }
    }
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

    gl.begin(GL10.GL_LINES);

    for (int i = 0; i <= gameGrid.gridSize.x; i++) {
      addPoint(i * gameGrid.unitSize.x, 0);
      addPoint(i * gameGrid.unitSize.x, gameGrid.gridSize.y * gameGrid.unitSize.y);
    }

    for (int i = 0; i <= gameGrid.gridSize.y; i++) {
      addPoint(0, i * gameGrid.unitSize.y);
      addPoint(gameGrid.gridSize.x * gameGrid.unitSize.x, i * gameGrid.unitSize.y);
    }

    gl.end();
  }

  private void addPoint(float x, float y) {
    gl.color(gameGrid.gridColor.r, gameGrid.gridColor.g, gameGrid.gridColor.b, gameGrid.gridColor.a);
    gl.vertex(x, y, 0.0f);
  }

  public void toggleGridLines() {
    shouldRenderGridLines = !shouldRenderGridLines;
  }
}
