/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.GridObjectPlacementState;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GridObjectState {
  private String typeId;
  private GridPoint position;
  private GridPoint size;

  public GridObjectState() {

  }

  public GridObjectState(GridObject gridObject) {
    typeId = gridObject.getGridObjectType().getId();
    position = gridObject.getPosition();
    size = gridObject.getSize();
  }

  public GridObject materialize(GameGrid gameGrid) {
    GridObjectType objectType = GridObjectTypeFactory.findTypeById(typeId);
    if (objectType != null) {
      GridObject object = objectType.makeGridObject(gameGrid);

      if (object != null) {
        object.setPosition(position.x, position.y);
        object.setSize(size.x, size.y);
        object.setPlacementState(GridObjectPlacementState.PLACED);

        gameGrid.addObject(object);

        return object;
      }
    }

    throw new RuntimeException("Cannot find type: "+ typeId);
  }

  public GridPoint getPosition() {
    return position;
  }

  public void setPosition(GridPoint position) {
    this.position = position;
  }

  public GridPoint getSize() {
    return size;
  }

  public void setSize(GridPoint size) {
    this.size = size;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }
}
