/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.collections;

import com.badlogic.gdx.utils.Array;
import com.google.common.collect.Maps;

import java.util.Map;

public class TypeInstanceMap<T> {
  private Array<T> instances;
  private Map<Class, Array<T>> instancesByType;

  public TypeInstanceMap() {
    instances = new Array<T>();
    instances.ordered = true;
    instancesByType = Maps.newHashMap();
  }

  public void add(T instance) {
    instances.add(instance);
    setForType(instance.getClass()).add(instance);
  }

  public void remove(T instance) {
    instances.removeValue(instance, false);
    setForType(instance.getClass()).removeValue(instance, false);
  }

  public Array<T> getInstances() {
    return instances;
  }

  public Array<T> setForType(Class instanceClass) {
    if (!instancesByType.containsKey(instanceClass)) {
      instancesByType.put(instanceClass, new Array<T>());
    }
    return instancesByType.get(instanceClass);
  }

  public void clear() {
    instancesByType.clear();
    instances.clear();
  }

  public boolean isEmpty() {
    return instances.size == 0 && instancesByType.isEmpty();
  }
}
