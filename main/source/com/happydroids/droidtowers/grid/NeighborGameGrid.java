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
    super();

    setGridOrigin(gridOrigin);
    setGridScale(0.3f, 0.3f);
    gameGridRenderer = new GameGridRenderer(this, camera);
  }

  public void findLimits() {
    Vector2 min = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    Vector2 max = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      GridPoint size = gridObject.getSize();

      min.x = Math.min(position.x, min.x);
      min.y = Math.min(position.y, min.y);

      max.x = Math.max(position.x + size.x, max.x);
      max.y = Math.max(position.y + size.y, max.y);
    }
    setGridSize(max.x - min.x, max.y - min.y);
    updateWorldSize();

    for (GridObject gridObject : getObjects()) {
      GridPoint position = gridObject.getPosition();
      gridObject.setPosition(position.x - min.x, position.y - min.y);
    }
  }

  public void setClickListener(Runnable clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    if (worldBounds.contains(worldPoint.x, worldPoint.y)) {
      clickListener.run();
      return true;
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
