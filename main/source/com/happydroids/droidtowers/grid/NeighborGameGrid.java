/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.actions.GameGridClickListener;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;

public class NeighborGameGrid extends GameGrid {
  private GameGridClickListener clickListener;
  private String ownerName;

  public NeighborGameGrid(OrthographicCamera camera, GridPoint gridOrigin) {
    super(camera);

    setGridOrigin(gridOrigin);
    setGridScale(0.3f);

    gameGridRenderer.toggleGridLines();
  }

  public void findLimits() {
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      GridPoint size = gridObject.getSize();

      minX = Math.min(position.x, minX);
      maxX = Math.max(position.x + size.x, maxX);
      maxY = Math.max(position.y + size.y, maxY);
    }
    setGridSize(maxX - minX, gridSize.y);
    updateWorldSize(false);

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      gridObject.setPosition(position.x - minX, position.y);
    }
  }

  public void addListener(GameGridClickListener clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    if (worldBounds.contains(worldPoint.x, worldPoint.y)) {
      for (GridObject gridObject : getObjects()) {
        if (gridObject.getWorldBounds().contains(worldPoint.x, worldPoint.y)) {
          clickListener.click(this, gridObject, worldPoint.x, worldPoint.y);
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
