package com.unhappyrobot.types;

import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.*;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  private boolean isLobby;
  private int populationMax;
  private int populationRequired;
  private ProviderType provides;

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
    Set<GridObject> objectsOverlapped = GridPositionCache.instance().getObjectsAt(gridObject.getPosition(), gridObject.getSize(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!object.canShareSpace(gridObject)) {
        return false;
      }
    }

    return true;
  }

  public boolean isLobby() {
    return isLobby;
  }

  public int getPopulationMax() {
    return populationMax;
  }

  public int getPopulationRequired() {
    return populationRequired;
  }

  public ProviderType provides() {
    return provides;
  }
}
