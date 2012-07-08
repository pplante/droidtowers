/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GridObjectState {
  private String typeId;
  private GridPoint position;
  private GridPoint size;
  private String name;
  private int loanFromCousinVinnie;

  public GridObjectState() {

  }

  public GridObjectState(GridObject gridObject) {
    typeId = gridObject.getGridObjectType().getId();
    position = gridObject.getPosition();
    size = gridObject.getSize();
    loanFromCousinVinnie = gridObject.getAmountLoanedFromCousinVinnie();
    name = gridObject.hasCustomName() ? gridObject.getName() : null;
  }

  public GridObject materialize(GameGrid gameGrid) {
    GridObjectType objectType = GridObjectTypeFactory.findTypeById(typeId);
    if (objectType != null) {
      GridObject object = objectType.makeGridObject(gameGrid);

      if (object != null) {
        if (name != null) {
          object.setName(name);
        }
        object.setPosition(position.x, position.y);
        object.setSize(size.x, size.y);
        object.setPlaced(true);
        object.addLoanFromCousinVinnie(loanFromCousinVinnie);

        gameGrid.addObject(object);

        return object;
      }
    }

    throw new RuntimeException("Cannot find type: " + typeId);
  }
}
