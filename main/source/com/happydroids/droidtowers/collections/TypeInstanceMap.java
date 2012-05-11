/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.collections;

import com.google.common.collect.Maps;
import com.happydroids.droidtowers.entities.GuavaSet;

import java.util.Map;

public class TypeInstanceMap<T> {
  private GuavaSet<T> instances;
  private Map<Class<T>, GuavaSet<T>> instancesByType;

  public TypeInstanceMap() {
    instances = new GuavaSet<T>();
    instancesByType = Maps.newHashMap();
  }

  public void add(T instance) {
    setForType(instance.getClass()).add(instance);
    instances.add(instance);
  }

  public void remove(T instance) {
    setForType(instance.getClass()).remove(instance);
    instances.remove(instance);
  }

  public GuavaSet<T> getInstances() {
    return instances;
  }

  public GuavaSet<T> setForType(Class instanceClass) {

    if (!instancesByType.containsKey(instanceClass)) {
      instancesByType.put(instanceClass, new GuavaSet<T>());
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
