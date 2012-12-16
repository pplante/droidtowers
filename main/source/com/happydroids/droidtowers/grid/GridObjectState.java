/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GridObjectState {
  private String typeId;
  private GridPoint position;
  private GridPoint size;
  private String name;
  private int variationId;
  private int loanFromCousinVinnie;
  private List<JobCandidate> employees;
  private int numberOfElevatorCars;

  public GridObjectState() {

  }

  public GridObjectState(GridObject gridObject) {
    typeId = gridObject.getGridObjectType().getId();
    position = gridObject.getPosition();
    size = gridObject.getSize();
    name = gridObject.hasCustomName() ? gridObject.getName() : null;
    variationId = gridObject.getVariationId();
    loanFromCousinVinnie = gridObject.getAmountLoanedFromCousinVinnie();

    if (gridObject instanceof Elevator) {
      numberOfElevatorCars = ((Elevator) gridObject).getNumElevatorCars();
    }
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
        if (variationId > 0) {
          object.setVariationId(variationId);
        }
        object.addLoanFromCousinVinnie(loanFromCousinVinnie);

        if (object instanceof Elevator) {
          ((Elevator) object).setNumElevatorCars(numberOfElevatorCars);
        }

        object.updateSprite();

        gameGrid.addObject(object);

        return object;
      }
    }

    throw new RuntimeException("Cannot find type: " + typeId);
  }
}
