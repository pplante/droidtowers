/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.pathfinding.AStar;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;

import java.util.ArrayList;

public class PathSearchManager {
  private static PathSearchManager _instance;

  private ArrayList<AStar> pathFinders;
  private AStar currentPathFinder;
  private int framesSinceUpdate;

  private PathSearchManager() {
    pathFinders = Lists.newArrayList();
  }


  public static PathSearchManager instance() {
    if (_instance == null) {
      _instance = new PathSearchManager();
    }

    return _instance;
  }

  public void queue(final AStar pathFinder) {
    pathFinders.add(pathFinder);
  }

  public void update(float deltaTime) {
    if (currentPathFinder != null) {
      if (currentPathFinder.isWorking()) {
        for (int i = 0; i < 50 && currentPathFinder.isWorking(); i++) {
          currentPathFinder.step();
        }
      } else {
        currentPathFinder.runCompleteCallback();
        currentPathFinder = null;
      }
    } else if (!pathFinders.isEmpty()) {
      currentPathFinder = pathFinders.remove(0);
    }
  }

  public void remove(TransitPathFinder pathFinder) {
    pathFinders.remove(pathFinder);

    if (pathFinder.equals(currentPathFinder)) {
      currentPathFinder = null;
    }

    pathFinder.cancel();
  }

  public void dispose() {
    _instance = null;
  }

  public int queueLength() {
    return 0;
  }
}
