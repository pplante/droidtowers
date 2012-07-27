/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.pathfinding.AStar;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;

import java.util.LinkedList;

public class PathSearchManager {
  private static PathSearchManager _instance;

  private LinkedList<AStar> pathFinders;
  private AStar currentPathFinder;

  private PathSearchManager() {
    pathFinders = Lists.newLinkedList();
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
        int times = 0;
        while (currentPathFinder.isWorking() && times < 25) {
          currentPathFinder.step();
          times += 1;
        }
      } else {
        currentPathFinder.runCompleteCallback();
        currentPathFinder = null;
      }
    } else if (!pathFinders.isEmpty()) {
      currentPathFinder = pathFinders.poll();
      currentPathFinder.start();
    }
  }

  public void remove(TransitPathFinder pathFinder) {
    pathFinders.remove(pathFinder);

    if (pathFinder.equals(currentPathFinder)) {
      currentPathFinder = null;
    }

    pathFinder.cancel();
  }
}
