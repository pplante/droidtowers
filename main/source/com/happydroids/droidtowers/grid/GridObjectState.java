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
  private GridPoint position;
  private GridPoint size;
  private Class<? extends GridObjectType> typeClass;
  private String typeName;

  public GridObjectState() {

  }

  public GridObjectState(GridObject gridObject) {
    typeClass = gridObject.getGridObjectType().getClass();
    typeName = gridObject.getGridObjectType().getName();
    position = gridObject.getPosition();
    size = gridObject.getSize();
  }

  public Class<? extends GridObjectType> getTypeClass() {
    return typeClass;
  }

  public GridObject materialize(GameGrid gameGrid) {
    GridObjectTypeFactory factoryForType = GridObjectTypeFactory.factoryForType(typeClass);

    if (factoryForType != null) {
      GridObjectType objectType = factoryForType.findByName(typeName);

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
    } else {
      throw new RuntimeException("Looks like you forget to call instance() of the factory for type: " + typeClass);
    }

    return null;
  }
}
