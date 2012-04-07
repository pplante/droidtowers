/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.pathfinding.AStar;

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

  public void queue(AStar pathFinder) {
    pathFinders.add(pathFinder);
  }

  public void update(float deltaTime) {
    if (currentPathFinder != null) {
      for (int i = 0; i < 50 && currentPathFinder.isWorking(); i++) {
        currentPathFinder.step();
      }

      if (!currentPathFinder.isWorking()) {
        currentPathFinder.runCompleteCallback();
        currentPathFinder = null;
      }
    } else if (!pathFinders.isEmpty()) {
      currentPathFinder = pathFinders.poll();
      currentPathFinder.start();
    }
  }
}
