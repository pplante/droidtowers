/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.PriorityQueue;

/**
 * A* algorithm implementation using the method design pattern.
 *
 * @author Giuseppe Scrivano
 */
public abstract class AStar<T> {

  protected Array<T> discoveredPath;
  protected boolean working;
  private Runnable completeCallback;
  protected T start;
  protected T goal;

  public boolean isWorking() {
    return working;
  }

  public void runCompleteCallback() {
    if (completeCallback != null) {
      completeCallback.run();
    }
  }

  public void cancel() {
    working = false;
    lastCost = Integer.MAX_VALUE;
    paths.clear();
  }

  public void setStart(T start) {
    this.start = start;
  }

  public void setGoal(T goal) {
    this.goal = goal;
  }

  public boolean isFinished() {
    return !isWorking() && lastCost != Integer.MAX_VALUE;
  }

  public Runnable getCompleteCallback() {
    return completeCallback;
  }


  private class Path implements Comparable {

    public T point;

    public int f;
    public int g;
    public Path parent;

    public Path() {
      parent = null;
      point = null;
      g = f = 0;
    }

    public Path(Path p) {
      this();
      parent = p;
      g = p.g;
      f = p.f;
    }

    /**
     * Compare to another object using the total cost f.
     */
    @SuppressWarnings("unchecked")
    public int compareTo(Object o) {
      Path p = (Path) o;
      return f - p.f;
    }

    /**
     * Get the last point on the path.
     */
    public T getPoint() {
      return point;
    }

    /**
     * Set the point
     *
     * @param p The node
     */
    public void setPoint(T p) {
      point = p;
    }

    public void setParent(Path parent) {
      this.parent = parent;
    }
  }

  /**
   * Check if the current node is a goal for the problem.
   */
  protected abstract boolean isGoal(T node);

  /**
   * Cost for the operation to go to <code>to</code> from
   * <code>from</from>.
   */
  protected abstract int g(T from, T to);

  /**
   * Estimated cost to reach a goal node.
   * An admissible heuristic never gives a cost bigger than the real
   * one.
   * <code>from</from>.
   */
  protected abstract int h(T from, T to);

  /**
   * Generate the successors for a given node.
   */
  protected abstract Array<T> generateSuccessors(T node);

  protected PriorityQueue<Path> paths;


  private ObjectIntMap<T> minDistances;


  protected int lastCost;
  private int expandedCounter;

  public int getExpandedCounter() {
    return expandedCounter;
  }

  public AStar() {
    paths = new PriorityQueue<Path>();
    minDistances = new ObjectIntMap<T>();
    expandedCounter = 0;
    lastCost = 0;
  }

  protected int f(Path p, T from, T to) {
    int g = g(from, to) + ((p.parent != null) ? p.parent.g : 0);
    int h = h(from, to);

    p.g = g;
    p.f = g + h;

    return p.f;
  }

  private void expand(Path path) {
    T p = path.getPoint();
    int min = minDistances.get(path.getPoint(), Integer.MAX_VALUE);

    /*
    * If a better path passing for this point already exists then
    * don't expand it.
    */
    if (min == Integer.MAX_VALUE || min > path.f) {
      minDistances.put(path.getPoint(), path.f);
    } else {
      return;
    }

    Array<T> successors = generateSuccessors(p);

    for (T t : successors) {
      Path newPath = new Path(path);
      newPath.setPoint(t);
      f(newPath, path.getPoint(), t);
      paths.offer(newPath);
    }

    expandedCounter++;
  }


  public int getCost() {
    return lastCost;
  }

  public void start() {
    try {
      working = true;

      paths.clear();
      minDistances.clear();
      expandedCounter = 0;
      lastCost = 0;

      Path root = new Path();
      root.setPoint(start);

      /* Needed if the initial point has a cost.  */
      f(root, start, start);

      expand(root);
      step();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void step() {
    if (!working) {
      return;
    }

    Path p = paths.poll();

    if (p == null) {
      lastCost = Integer.MAX_VALUE;
      working = false;
      return;
    }

    T last = p.getPoint();

    lastCost = p.g;

    if (isGoal(last)) {
      discoveredPath = new Array<T>(true, 20);
      for (Path i = p; i != null; i = i.parent) {
        T point = i.getPoint();
        discoveredPath.insert(0, point);
      }

      working = false;
      return;
    }

    expand(p);
  }

  public void setCompleteCallback(Runnable completeCallback) {
    this.completeCallback = completeCallback;
  }

  public boolean wasSuccessful() {
    return lastCost != Integer.MAX_VALUE;
  }

  public Array<T> getDiscoveredPath() {
    return discoveredPath;
  }
}
