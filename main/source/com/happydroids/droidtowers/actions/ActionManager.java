/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.actions;

import java.util.concurrent.ConcurrentHashMap;

public class ActionManager {
  private static ActionManager instance_;
  private ConcurrentHashMap<Integer, Action> actions;

  public static ActionManager instance() {
    if (instance_ == null) {
      instance_ = new ActionManager();
    }

    return instance_;
  }

  private ActionManager() {
    actions = new ConcurrentHashMap<Integer, Action>();
  }

  public void addAction(Action action) {
    actions.put(action.hashCode(), action);
  }

  public void update(float deltaTime) {
    for (Action action : actions.values()) {
      action.act(deltaTime);

      if (action.isMarkedForRemoval()) {
        removeAction(action);
      }
    }
  }

  public void removeAction(Action action) {
    actions.remove(action.hashCode());
  }
}
