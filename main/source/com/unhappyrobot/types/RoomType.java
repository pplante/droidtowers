package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Room;
import com.unhappyrobot.math.Bounds2d;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  private boolean isLobby;

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Room(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    Room room = (Room) gridObject;

    if (isLobby) {
      return room.position.y == 4;
    } else {
      Bounds2d belowObject = new Bounds2d(room.position.cpy().sub(0, 1), room.size);

      List<GridObject> position = room.getGameGrid().getObjectsAt(belowObject);
      if (position.size() == 0) {
        return false;
      }
    }

    return true;
  }
}
