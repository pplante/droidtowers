/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import static java.lang.Math.abs;

public class NeighborGameGrid extends GameGrid {
  public NeighborGameGrid(OrthographicCamera camera, Vector2 gridOrigin) {
    super();
    setGridOrigin(gridOrigin);
    gameGridRenderer = new NeighborGameGridRenderer(this, camera);
  }

  private class NeighborGameGridRenderer extends GameGridRenderer {
    private Matrix4 transformMatrix;
    private float zoom = 4f;
    private Matrix4 projectionMatrix;
    private Matrix4 previousTransformMatrix;
    private Matrix4 previousProjectionMatrix;

    public NeighborGameGridRenderer(NeighborGameGrid neighborGameGrid, OrthographicCamera camera) {
      super(neighborGameGrid, camera);

      projectionMatrix = new Matrix4();
      transformMatrix = new Matrix4();
      Vector2 gridOrigin = neighborGameGrid.getGridOrigin();
      transformMatrix.translate(gridOrigin.x, gridOrigin.y, 0f);

      previousProjectionMatrix = new Matrix4();
      previousTransformMatrix = new Matrix4();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
      previousTransformMatrix.set(spriteBatch.getTransformMatrix().cpy());
      previousProjectionMatrix.set(spriteBatch.getProjectionMatrix().cpy());

      try {
        projectionMatrix.setToOrtho(zoom * -camera.viewportWidth / 2,
                                           zoom * camera.viewportWidth / 2,
                                           zoom * -camera.viewportHeight / 2,
                                           zoom * camera.viewportHeight / 2,
                                           abs(camera.near),
                                           abs(camera.far));

        spriteBatch.setTransformMatrix(transformMatrix);
        spriteBatch.setProjectionMatrix(projectionMatrix);
        shapeRenderer.setTransformMatrix(transformMatrix);
        shapeRenderer.setProjectionMatrix(projectionMatrix);

        super.render(spriteBatch);
      } finally {
        spriteBatch.setTransformMatrix(previousTransformMatrix);
        spriteBatch.setProjectionMatrix(previousProjectionMatrix);
      }
    }
  }
}
