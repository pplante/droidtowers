package com.unhappyrobot.events;

import com.unhappyrobot.entities.GameGrid;

import java.util.EventObject;

public class GameGridResizeEvent extends EventObject {
  public GameGridResizeEvent(GameGrid gameGrid) {
    super(gameGrid);
  }
}
