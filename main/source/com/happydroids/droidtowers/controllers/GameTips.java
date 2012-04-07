/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.events.GridObjectAddedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;

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
