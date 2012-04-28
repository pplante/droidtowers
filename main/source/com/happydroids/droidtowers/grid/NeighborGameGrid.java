/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;

public class NeighborGameGrid extends GameGrid {
  private Runnable clickListener;
  private String ownerName;

  public NeighborGameGrid(OrthographicCamera camera, Vector2 gridOrigin) {
    super(camera);

    setGridOrigin(gridOrigin);
    setGridScale(0.3f);

    gameGridRenderer.toggleGridLines();
  }

  public void findLimits() {
    float minX = Float.MAX_VALUE;
    float maxX = Float.MIN_VALUE;
    float maxY = Float.MIN_VALUE;

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      GridPoint size = gridObject.getSize();

      minX = Math.min(position.x, minX);
      maxX = Math.max(position.x + size.x, maxX);
      maxY = Math.max(position.y + size.y, maxY);
    }
    setGridSize(maxX - minX, maxY);
    updateWorldSize();

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      gridObject.setPosition(position.x - minX, position.y);
    }
  }

  public void setClickListener(Runnable clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    if (worldBounds.contains(worldPoint.x, worldPoint.y)) {
      for (GridObject gridObject : getObjects()) {
        if (gridObject.getWorldBounds().contains(worldPoint.x, worldPoint.y)) {
          clickListener.run();
          return true;
        }
      }
    }

    return false;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getOwnerName() {
    return ownerName;
  }
}
