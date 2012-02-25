package com.unhappyrobot.events;

import com.google.common.eventbus.EventBus;

public class GameEvents {
  private EventBus eventBus;
  private static GameEvents instance;

  private GameEvents() {
    eventBus = new EventBus("GameEvents");
  }

  static GameEvents instance() {
    if (instance == null) {
      instance = new GameEvents();
    }

    return instance;
  }

  public static void post(Object event) {
    instance().eventBus.post(event);
  }

  public static void register(Object listener) {
    instance().eventBus.register(listener);
  }

  public static void unregister(Object listener) {
    instance().eventBus.unregister(listener);
  }
}
