package com.unhappyrobot.types;

import com.unhappyrobot.entities.*;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  private boolean isLobby;
  private int maxPopulation;
  private int populationRequired;

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Room(this, gameGrid);
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Elevator || gridObject instanceof Stair;
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    if (isLobby && gridObject.getPosition().y == 4) {
      return checkForOverlap(gridObject);
    }

    return checkIfTouchingAnotherObject(gridObject) && checkForOverlap(gridObject);
  }

  @Override
  protected boolean checkForOverlap(GridObject gridObject) {
    List<GridObject> objectsOverlapped = gridObject.getGameGrid().getObjectsAt(gridObject.getBounds(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!gridObject.canShareSpace(object)) {
        return false;
      }
    }

    return objectsOverlapped.size() == 0;
  }

  public boolean isLobby() {
    return isLobby;
  }

  public int getMaxPopulation() {
    return maxPopulation;
  }

  public int getPopulationRequired() {
    return populationRequired;
  }
}
