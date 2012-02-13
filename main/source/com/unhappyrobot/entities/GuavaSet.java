package com.unhappyrobot.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.HashSet;

public class GuavaSet<T> extends HashSet<T> {
  public GuavaSet() {

  }

  public GuavaSet(int initialCapacity) {
    super(initialCapacity);
  }

  public void filterBy(Predicate<T> predicate) {
    Iterables.filter(this, predicate);
  }
}
