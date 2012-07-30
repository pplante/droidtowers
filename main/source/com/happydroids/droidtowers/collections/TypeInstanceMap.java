/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.collections;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class TypeInstanceMap<T> {
  private LinkedList<T> instances;
  private Map<Class, ArrayList<T>> instancesByType;

  public TypeInstanceMap() {
    instances = Lists.newLinkedList();
    instancesByType = Maps.newHashMap();
  }

  public void add(T instance) {
    instances.add(instance);
    setForType(instance.getClass()).add(instance);
  }

  public void remove(T instance) {
    instances.remove(instance);
    setForType(instance.getClass()).remove(instance);
  }

  public LinkedList<T> getInstances() {
    return instances;
  }

  public ArrayList<T> setForType(Class instanceClass) {
    if (!instancesByType.containsKey(instanceClass)) {
      instancesByType.put(instanceClass, new ArrayList<T>());
    }
    return instancesByType.get(instanceClass);
  }

  public void clear() {
    instancesByType.clear();
    instances.clear();
  }

  public boolean isEmpty() {
    return instances.isEmpty() && instancesByType.isEmpty();
  }
}
