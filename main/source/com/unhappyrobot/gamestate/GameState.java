package com.unhappyrobot.gamestate;

import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectChangedEvent;
import com.unhappyrobot.gamestate.actions.*;

public class GameState extends EventListener {
  private final GameGrid gameGrid;
  private final GameStateAction calculateTransportConnectionsAction;
  private final GameStateAction calculatePopulation;
  private final GameStateAction calculateJobs;
  private final GameStateAction calculateEarnout;

  public GameState(final GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    gameGrid.addEventListener(this);
    calculatePopulation = new PopulationCalculator(this.gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    calculateEarnout = new EarnoutCalculator(this.gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    calculateJobs = new EmploymentCalculator(this.gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    calculateTransportConnectionsAction = new TransportCalculator(this.gameGrid, 250);
  }

  public void update(float deltaTime, GameGrid gameGrid) {
    long currentTime = System.currentTimeMillis();

    calculateTransportConnectionsAction.act(currentTime);
    calculatePopulation.act(currentTime);
    calculateJobs.act(currentTime);
    calculateEarnout.act(currentTime);
  }

  public void receiveEvent(GridObjectAddedEvent e) {
    calculateTransportConnectionsAction.resetInterval();
  }

  public void receiveEvent(GridObjectChangedEvent e) {
    calculateTransportConnectionsAction.resetInterval();
  }
}
