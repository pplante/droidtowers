/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.google.common.collect.Lists;

import java.util.LinkedList;

public class RunnableQueue {
  private LinkedList<Runnable> queue;

  public RunnableQueue() {
    queue = Lists.newLinkedList();
  }

  public void push(Runnable runnable) {
    queue.add(runnable);
  }

  public void runAll() {
    while (queue.peek() != null) {
      queue.poll().run();
    }
  }

  public void clear() {
    queue.clear();
  }
}
