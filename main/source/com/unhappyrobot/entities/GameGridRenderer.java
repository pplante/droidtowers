package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;

public class GameGridRenderer extends GameLayer {
  private ImmediateModeRenderer10 gl;
  private GameGrid gameGrid;
  private boolean shouldRenderGridLines;

  public GameGridRenderer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    shouldRenderGridLines = true;
    gl = new ImmediateModeRenderer10();
  }

  @Override
  public void render(SpriteBatch spriteBatch, Camera camera) {
    if (shouldRenderGridLines) {
      renderGridLines();
    }

    renderGridObjects(spriteBatch);
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
