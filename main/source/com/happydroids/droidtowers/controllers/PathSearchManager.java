/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.Gdx;
import com.google.common.collect.Queues;
import com.happydroids.droidtowers.pathfinding.AStar;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PathSearchManager {
  private static PathSearchManager _instance;

  private ConcurrentLinkedQueue<AStar> pathFinders;
  private AStar currentPathFinder;
  private int framesSinceUpdate;


  private PathSearchManager() {
    pathFinders = Queues.newConcurrentLinkedQueue();
  }


  public static PathSearchManager instance() {
    if (_instance == null) {
      _instance = new PathSearchManager();
    }

    return _instance;
  }

  public synchronized void queue(final AStar pathFinder) {
    pathFinders.add(pathFinder);
  }

  public void update(float deltaTime) {
    if (framesSinceUpdate++ < 2) {
      return;
    }

    framesSinceUpdate = 0;
    if (currentPathFinder != null) {
      if (currentPathFinder.isWorking()) {
        currentPathFinder.step();
      } else {
        final AStar pathFinder = currentPathFinder;
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            pathFinder.runCompleteCallback();
          }
        });
        currentPathFinder = null;
      }
    } else if (!pathFinders.isEmpty()) {
      currentPathFinder = pathFinders.poll();
    }
  }

  public synchronized void remove(TransitPathFinder pathFinder) {
    pathFinders.remove(pathFinder);

    if (pathFinder.equals(currentPathFinder)) {
      currentPathFinder = null;
    }

    pathFinder.cancel();
  }

  public void dispose() {
    _instance = null;
  }
}
