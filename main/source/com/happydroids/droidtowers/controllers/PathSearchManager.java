/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.Gdx;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.pathfinding.AStar;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class PathSearchManager {
  private static PathSearchManager _instance;

  private ArrayList<AStar> pathFinders;
  private AStar currentPathFinder;
  private int framesSinceUpdate;
  private final ExecutorService threadPool;


  private PathSearchManager() {
    pathFinders = Lists.newArrayList();
    threadPool = Executors.newFixedThreadPool(2, new ThreadFactory() {
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "PathSearchManagerWorkThread");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        return thread;
      }
    });
  }


  public static PathSearchManager instance() {
    if (_instance == null) {
      _instance = new PathSearchManager();
    }

    return _instance;
  }

  public void queue(final AStar pathFinder) {
    threadPool.submit(new Runnable() {
      @Override
      public void run() {
        while (pathFinder.isWorking()) {
          pathFinder.step();
          Thread.yield();
        }

        Gdx.app.postRunnable(pathFinder.getCompleteCallback());
      }
    });
  }

  public void update(float deltaTime) {
//    if (currentPathFinder != null) {
//      if (currentPathFinder.isWorking()) {
//        for (int i = 0; i < 50 && currentPathFinder.isWorking(); i++) {
//          currentPathFinder.step();
//        }
//      } else {
//        currentPathFinder.runCompleteCallback();
//        currentPathFinder = null;
//      }
//    } else if (!pathFinders.isEmpty()) {
//      currentPathFinder = pathFinders.remove(0);
//    }
  }

  public void remove(TransitPathFinder pathFinder) {
//    pathFinders.remove(pathFinder);
//
//    if (pathFinder.equals(currentPathFinder)) {
//      currentPathFinder = null;
//    }
//
//    pathFinder.cancel();
  }

  public void dispose() {
    _instance = null;
  }

  public int queueLength() {
    return 0;
  }
}
