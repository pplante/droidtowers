package com.unhappyrobot.events;

import com.google.common.eventbus.EventBus;

public class GameEvents {
  private EventBus eventBus;
  private static GameEvents instance;

  private GameEvents() {
    eventBus = new EventBus("GameEvents");
  }

  static GameEvents getInstance() {
    if (instance == null) {
      instance = new GameEvents();
    }

    return instance;
  }

  public static void post(Object event) {
    getInstance().eventBus.post(event);
  }

  public static void register(Object listener) {
    getInstance().eventBus.register(listener);
  }
}
