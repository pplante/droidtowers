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
    renderGridObjects(spriteBatch);

    if (shouldRenderGridLines) {
      renderGridLines();
    }
  }

  private void renderGridObjects(SpriteBatch spriteBatch) {
    spriteBatch.begin();
    for (GridObject child : gameGrid.getObjects()) {
      child.render(spriteBatch);
    }
    spriteBatch.end();
  }

  private void renderGridLines() {
    Gdx.gl10.glEnable(GL10.GL_BLEND);
    Gdx.gl10.glDisable(GL10.GL_DEPTH_TEST);
    gl.begin(GL10.GL_LINES);

    for (int i = 0; i <= gameGrid.gridSize.x; i++) {
      addPoint(gameGrid.gridOrigin.x + (i * gameGrid.unitSize.x), gameGrid.gridOrigin.y);
      addPoint(gameGrid.gridOrigin.x + (i * gameGrid.unitSize.x), gameGrid.gridOrigin.y + gameGrid.gridSize.y * gameGrid.unitSize.y);
    }

    for (int i = 0; i <= gameGrid.gridSize.y; i++) {
      addPoint(gameGrid.gridOrigin.x, gameGrid.gridOrigin.y + (i * gameGrid.unitSize.y));
      addPoint(gameGrid.gridOrigin.x + gameGrid.gridSize.x * gameGrid.unitSize.x, gameGrid.gridOrigin.y + (i * gameGrid.unitSize.y));
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
