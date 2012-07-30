/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A* algorithm implementation using the method design pattern.
 *
 * @author Giuseppe Scrivano
 */
public abstract class AStar<T> {

  protected LinkedList<T> discoveredPath;
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
    lastCost = Double.MAX_VALUE;
    paths.clear();
  }

  public void setStart(T start) {
    this.start = start;
  }

  public void setGoal(T goal) {
    this.goal = goal;
  }

  public boolean isFinished() {
    return !isWorking() && lastCost != Double.MAX_VALUE;
  }


  private class Path implements Comparable {

    public T point;

    public Double f;
    public Double g;
    public Path parent;

    public Path() {
      parent = null;
      point = null;
      g = f = 0.0;
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
      return (int) (f - p.f);
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

  }

  /**
   * Check if the current node is a goal for the problem.
   */
  protected abstract boolean isGoal(T node);

  /**
   * Cost for the operation to go to <code>to</code> from
   * <code>from</from>.
   */
  protected abstract Double g(T from, T to);

  /**
   * Estimated cost to reach a goal node.
   * An admissible heuristic never gives a cost bigger than the real
   * one.
   * <code>from</from>.
   */
  protected abstract Double h(T from, T to);

  /**
   * Generate the successors for a given node.
   */
  protected abstract List<T> generateSuccessors(T node);

  protected PriorityQueue<Path> paths;


  private HashMap<T, Double> minDistances;


  protected Double lastCost;
  private int expandedCounter;

  public int getExpandedCounter() {
    return expandedCounter;
  }

  public AStar() {
    paths = new PriorityQueue<Path>();
    minDistances = new HashMap<T, Double>();
    expandedCounter = 0;
    lastCost = 0.0;
  }

  protected Double f(Path p, T from, T to) {
    Double g = g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
    Double h = h(from, to);

    p.g = g;
    p.f = g + h;

    return p.f;
  }

  private void expand(Path path) {
    T p = path.getPoint();
    Double min = minDistances.get(path.getPoint());

    /*
    * If a better path passing for this point already exists then
    * don't expand it.
    */
    if (min == null || min > path.f)
      minDistances.put(path.getPoint(), path.f);
    else
      return;

    List<T> successors = generateSuccessors(p);

    for (T t : successors) {
      Path newPath = new Path(path);
      newPath.setPoint(t);
      f(newPath, path.getPoint(), t);
      paths.offer(newPath);
    }

    expandedCounter++;
  }


  public Double getCost() {
    return lastCost;
  }

  public void start() {
    try {
      working = true;

      paths.clear();
      minDistances.clear();
      expandedCounter = 0;
      lastCost = 0.0;

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
    if (!working) return;

    Path p = paths.poll();

    if (p == null) {
      lastCost = Double.MAX_VALUE;
      working = false;
      return;
    }

    T last = p.getPoint();

    lastCost = p.g;

    if (isGoal(last)) {
      discoveredPath = new LinkedList<T>();
      for (Path i = p; i != null; i = i.parent) {
        T point = i.getPoint();
        discoveredPath.addFirst(point);
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
    return lastCost != Double.MAX_VALUE;
  }

  public LinkedList<T> getDiscoveredPath() {
    return discoveredPath;
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AStar)) return false;

    AStar aStar = (AStar) o;

    if (expandedCounter != aStar.expandedCounter) return false;
    if (working != aStar.working) return false;
    if (completeCallback != null ? !completeCallback.equals(aStar.completeCallback) : aStar.completeCallback != null)
      return false;
    if (discoveredPath != null ? !discoveredPath.equals(aStar.discoveredPath) : aStar.discoveredPath != null)
      return false;
    if (goal != null ? !goal.equals(aStar.goal) : aStar.goal != null) return false;
    if (lastCost != null ? !lastCost.equals(aStar.lastCost) : aStar.lastCost != null) return false;
    if (minDistances != null ? !minDistances.equals(aStar.minDistances) : aStar.minDistances != null) return false;
    if (paths != null ? !paths.equals(aStar.paths) : aStar.paths != null) return false;
    if (start != null ? !start.equals(aStar.start) : aStar.start != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = discoveredPath != null ? discoveredPath.hashCode() : 0;
    result = 31 * result + (working ? 1 : 0);
    result = 31 * result + (completeCallback != null ? completeCallback.hashCode() : 0);
    result = 31 * result + (start != null ? start.hashCode() : 0);
    result = 31 * result + (goal != null ? goal.hashCode() : 0);
    result = 31 * result + (paths != null ? paths.hashCode() : 0);
    result = 31 * result + (minDistances != null ? minDistances.hashCode() : 0);
    result = 31 * result + (lastCost != null ? lastCost.hashCode() : 0);
    result = 31 * result + expandedCounter;
    return result;
  }
}
