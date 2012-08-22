/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.grid.GameGrid;

import static com.happydroids.droidtowers.TowerConsts.LOBBY_FLOOR;
import static com.happydroids.droidtowers.types.ProviderType.LOBBY;
import static com.happydroids.droidtowers.types.ProviderType.SKY_LOBBY;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  public static final String HOUSING_STATS_LINE = "MAX RESIDENTS: {maxResidents}\nMAX INCOME: {maxIncome}";
  private boolean isLobby;
  private int populationMax;
  private int populationRequired;

  public RoomType() {
    statsLine = HOUSING_STATS_LINE;
  }

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    if (provides == LOBBY) {
      return new Lobby(this, gameGrid);
    } else if (provides == SKY_LOBBY) {
      return new SkyLobby(this, gameGrid);
    }

    return new Room(this, gameGrid);
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Elevator || gridObject instanceof Stair;
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    int yPos = gridObject.getPosition().y;
    if (provides == LOBBY) {
      return yPos == LOBBY_FLOOR && checkForOverlap(gridObject);
    } else if (provides == SKY_LOBBY) {
      if (yPos <= LOBBY_FLOOR || (yPos - LOBBY_FLOOR) % 15 != 0) {
        return false;
      }
    }

    return checkIfTouchingAnotherObject(gridObject) && checkForOverlap(gridObject);
  }

  @Override
  protected boolean checkForOverlap(GridObject gridObject) {
    Array<GridObject> objectsOverlapped = gridObject.getGameGrid()
                                                  .positionCache()
                                                  .getObjectsAt(gridObject.getPosition(), gridObject.getSize(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!object.canShareSpace(gridObject)) {
        return false;
      }
    }

    return true;
  }

  public int getPopulationMax() {
    return populationMax;
  }

  public int getPopulationRequired() {
    return populationRequired;
  }
}
