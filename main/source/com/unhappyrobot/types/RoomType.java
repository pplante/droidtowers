package com.unhappyrobot.types;

import com.unhappyrobot.entities.*;
import com.unhappyrobot.math.Bounds2d;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  private boolean isLobby;
  private int maxPopulation;

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
    Room room = (Room) gridObject;

    if (isLobby) {
      return room.getPosition().y == 4;
    } else {
      Bounds2d belowObject = new Bounds2d(room.getPosition().cpy().sub(0, 1), room.getSize());

      List<GridObject> position = room.getGameGrid().getObjectsAt(belowObject);
      if (position.size() == 0) {
        return false;
      }
    }

    return true;
  }

  public boolean isLobby() {
    return isLobby;
  }

  public int getMaxPopulation() {
    return maxPopulation;
  }
}
