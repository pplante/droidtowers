/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.actions;

import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Set;

public class ActionManager {
  private static ActionManager instance_;
  private Set<Action> actions;

  public static ActionManager instance() {
    if (instance_ == null) {
      instance_ = new ActionManager();
    }

    return instance_;
  }

  private ActionManager() {
    actions = Sets.newHashSet();
  }

  public void addAction(Action action) {
    actions.add(action);
  }

  public void update(float deltaTime) {
    Iterator<Action> actionIterator = actions.iterator();

    while (actionIterator.hasNext()) {
      Action action = actionIterator.next();
      action.act(deltaTime);

      if (action.isMarkedForRemoval()) {
        actionIterator.remove();
      }
    }
  }

  public void removeAction(Action action) {
    actions.remove(action);
  }
}
