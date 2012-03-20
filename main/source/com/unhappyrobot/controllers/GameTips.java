package com.unhappyrobot.controllers;

import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.gui.HeadsUpDisplay;

public class GameTips {
  private static GameTips instance;
  private boolean enabled;

  private GameTips() {
//    TODO: FIX
//    GameGrid.events().register(this);
  }

  public static GameTips instance() {
    if (instance == null) {
      instance = new GameTips();
    }

    return instance;
  }

  @Subscribe
  public void GameGrid_objectAdded(GridObjectAddedEvent event) {
    if (!enabled) return;

    if (event.gridObject instanceof Elevator) {
      HeadsUpDisplay.instance().showTipBubble(event.gridObject, "Drag the top or bottom,\nto service other floors.");
    }
  }

  public void enable() {
    enabled = true;
  }
}
