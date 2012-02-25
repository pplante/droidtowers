package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Room;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.math.GridPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DesirabilityCalculator extends GameStateAction {
  private HashMap<GridPoint, List<Float>> noisyPositions;

  public DesirabilityCalculator(GameGrid gameGrid, float roomUpdateFrequency) {
    super(gameGrid, roomUpdateFrequency);

    noisyPositions = new HashMap<GridPoint, List<Float>>();
  }

  @Override
  public void run() {
    noisyPositions.clear();

    for (GridObject gridObject : gameGrid.getObjects()) {
      float noiseLevel = gridObject.getNoiseLevel();
      if (noiseLevel > 0) {
        GridPoint position = gridObject.getPosition();
        GridPoint size = gridObject.getSize();
        for (float x = -2; x < size.x + 2; x += 1f) {
          for (float y = -2; y < size.y + 2; y += 1f) {
            float xSum = (x > 0 && x < size.x ? x : x - size.x) - x;
            float ySum = (y > 0 && y < size.y ? y : y - size.y) - y;
            float distance = (float) Math.sqrt((xSum * xSum) + (ySum * ySum));
            if (distance > 0.1f) {
              getListForPosition(new GridPoint(position.x + x, position.y + y)).add(noiseLevel * distance);
            }
          }
        }
      }
    }

    Set<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;

        float total = 0;
        int count = 0;
        for (GridPoint gridPoint : gridObject.getGridPointsOccupied()) {
          if (noisyPositions.containsKey(gridPoint)) {
            List<Float> floats = noisyPositions.get(gridPoint);
            count += floats.size();
            for (Float val : floats) {
              total += val;
            }
          }
        }
        if (count > 0f) {
          room.setSurroundingNoiseLevel(0.9f * (total / count));
        } else {
          room.setSurroundingNoiseLevel(0.9f);
        }
      }
    }
  }

  private List<Float> getListForPosition(GridPoint gridPoint) {
    List<Float> values;
    if (!noisyPositions.containsKey(gridPoint)) {
      values = new ArrayList<Float>();
      noisyPositions.put(gridPoint, values);
    } else {
      values = noisyPositions.get(gridPoint);
    }
    return values;
  }
}
