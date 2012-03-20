package com.unhappyrobot.controllers;

import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.gui.HeadsUpDisplay;

public class GameTips {
  private static GameTips instance;
  private boolean enabled;

  public GameTips(GameGrid gameGrid) {
    gameGrid.events().register(this);
  }

  @Subscribe
  public void GameGrid_objectAdded(GridObjectAddedEvent event) {
    if (event.gridObject instanceof Elevator) {
      HeadsUpDisplay.instance().showTipBubble(event.gridObject, "Drag the top or bottom,\nto service other floors.");
    }
  }
}
