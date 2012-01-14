package com.unhappyrobot.managers;

import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectChangedEvent;
import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.types.RoomType;

import java.util.Set;

@SuppressWarnings("unchecked")
public class GameState extends EventListener {
  private final GameGrid gameGrid;
  private final TimeDelayedAction calculateTransportConnectionsAction = new TimeDelayedAction(250, false) {
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
      Set<GridObject> transports = gameGrid.getInstancesOf(Elevator.class, Stair.class);
      Set<GridObject> rooms = gameGrid.getInstancesOf(Room.class, CommercialSpace.class);

      if (transports == null) {
        return;
      }

      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        RoomType roomType = (RoomType) room.getGridObjectType();
        if (!roomType.isLobby()) {
          room.setConnectedToTransport(false);
        }

        for (GridObject transport : transports) {
          if (room.getPosition().y >= transport.getContentPosition().y && room.getPosition().y <= transport.getContentPosition().y + transport.getContentSize().y) {
            room.setConnectedToTransport(true);
            break;
          }
        }
      }
    }
  };

  private final TimeDelayedAction calculatePopulation = new TimeDelayedAction(TowerConsts.ROOM_UPDATE_FREQUENCY) {
    @Override
    public void run() {
      Set<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
      Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
      int currentResidency = 0;
      int attractedPopulation = 0;

      if (rooms != null) {
        for (GridObject gridObject : rooms) {
          Room room = (Room) gridObject;

          room.updatePopulation();
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

      Player.getInstance().setPopulationResidency(currentResidency);
      Player.getInstance().setPopulationAttracted(currentResidency);
    }
  };

  private final TimeDelayedAction calculateJobs = new TimeDelayedAction(TowerConsts.JOB_UPDATE_FREQUENCY) {
    @Override
    public void run() {
      Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
      int jobsFilled = 0;
      if (commercialSpaces != null) {
        for (GridObject gridObject : commercialSpaces) {
          CommercialSpace commercialSpace = (CommercialSpace) gridObject;
          commercialSpace.updateJobs();

          jobsFilled += commercialSpace.getJobsFilled();
        }
      }

      Player.getInstance().setJobsFilled(jobsFilled);
    }
  };

  private final TimeDelayedAction calculateEarnout = new TimeDelayedAction(TowerConsts.PLAYER_EARNOUT_FREQUENCY) {
    @Override
    public void run() {
      int coinsEarned = 0;
      for (GridObject object : gameGrid.getObjects()) {
        coinsEarned += object.getCoinsEarned();
      }

      System.out.println(String.format("Player earned: %d coins", coinsEarned));
      Player.getInstance().addCurrency(coinsEarned);
    }
  };

  public GameState(final GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    gameGrid.addEventListener(this);
  }

  public void update(float deltaTime, GameGrid gameGrid) {
    long currentTime = System.currentTimeMillis();

    calculateTransportConnectionsAction.act(currentTime);
    calculatePopulation.act(currentTime);
    calculateJobs.act(currentTime);
  }

  public void receiveEvent(GridObjectAddedEvent e) {
    calculateTransportConnectionsAction.resetInterval();

    int maxPopulation = 0;
    int maxJobs = 0;
    for (GridObject gridObject : gameGrid.getInstancesOf(Room.class, CommercialSpace.class)) {
      maxPopulation += ((RoomType) gridObject.getGridObjectType()).getPopulationMax();

      if (gridObject instanceof CommercialSpace) {
        maxJobs += ((CommercialType) gridObject.getGridObjectType()).getJobsProvided();
      }
    }

    Player.getInstance().setPopulationMax(maxPopulation);
    Player.getInstance().setJobsMax(maxJobs);
  }

  public void receiveEvent(GridObjectChangedEvent e) {
    calculateTransportConnectionsAction.resetInterval();
  }
}
