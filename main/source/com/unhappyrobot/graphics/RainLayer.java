package com.unhappyrobot.graphics;

import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.Rain;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GameGridResizeEvent;

public class RainLayer extends GameLayer {
  public RainLayer() {
    GameEvents.register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    addChild(new Rain(event.gameGrid));
    addChild(new Rain(event.gameGrid));
  }

}
