package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.entities.Room;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.RoomType;

import java.util.Set;

public class PopulationCalculator extends GameStateAction {
  public PopulationCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    Set<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    int currentResidency = 0;
    int attractedPopulation = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        room.updatePopulation();

        maxPopulation += ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
        currentResidency += room.getCurrentResidency();
      }
    }

    if (commercialSpaces != null) {
      for (GridObject gridObject : commercialSpaces) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        commercialSpace.updatePopulation();

        attractedPopulation += commercialSpace.getAttractedPopulation();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setPopulationResidency(currentResidency);
    Player.instance().setPopulationAttracted(currentResidency);
  }
}
