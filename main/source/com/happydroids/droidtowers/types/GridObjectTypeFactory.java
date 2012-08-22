/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class GridObjectTypeFactory<T extends GridObjectType> {
  protected List<T> objectTypes;
  private Class<T> gridObjectTypeClass;
  private static List<GridObjectTypeFactory> typeFactories = new ArrayList<GridObjectTypeFactory>();

  public GridObjectTypeFactory(Class<T> gridObjectTypeClass) {
    this.gridObjectTypeClass = gridObjectTypeClass;

    typeFactories.add(this);
  }

  protected boolean parseTypesFile(FileHandle fileHandle) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      List<T> newObjectTypes = mapper.readValue(fileHandle.reader(), mapper.getTypeFactory()
                                                                             .constructCollectionType(ArrayList.class, gridObjectTypeClass));

      if (objectTypes != null) {
        objectTypes.addAll(newObjectTypes);
      } else {
        objectTypes = newObjectTypes;
      }

      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<T> all() {
    return objectTypes;
  }

  @SuppressWarnings("unchecked")
  public T findByName(String name) {
    for (GridObjectType type : objectTypes) {
      if (type.getName().equals(name)) {
        return (T) type;
      }
    }

    return null;
  }

  public static GridObjectTypeFactory factoryForType(Class<? extends GridObjectType> typeClass) {
    for (GridObjectTypeFactory typeFactory : typeFactories) {
      if (typeFactory.gridObjectTypeClass.equals(typeClass)) {
        return typeFactory;
      }
    }

    return null;
  }

  public static GridObjectType findTypeById(String typeId) {
    for (GridObjectTypeFactory typeFactory : typeFactories) {
      for (Object objectType : typeFactory.all()) {
        GridObjectType gridObjectType = (GridObjectType) objectType;
        if (gridObjectType.getId() != null && gridObjectType.getId().equalsIgnoreCase(typeId)) {
          return gridObjectType;
        }
      }
    }

    return null;
  }

  public static Set<GridObjectType> findByProviderTypeFromAnyFactory(ProviderType providerType) {
    Set<GridObjectType> objectTypes = Sets.newHashSet();

    for (GridObjectTypeFactory typeFactory : typeFactories) {
      for (Object o : typeFactory.all()) {
        GridObjectType gridObjectType = (GridObjectType) o;
        if (gridObjectType.provides(providerType)) {
          objectTypes.add(gridObjectType);
        }
      }
    }

    return objectTypes;
  }

  public Class<T> getObjectType() {
    return gridObjectTypeClass;
  }

  public T castToObjectType(Object o) {
    return gridObjectTypeClass.cast(o);
  }

  public List<T> findByProviderType(ProviderType providerType) {
    List<T> types = new ArrayList<T>();

    for (T objectType : all()) {
      if (objectType.provides(providerType)) {
        types.add(objectType);
      }
    }

    return types;
  }

  public static List<GridObjectTypeFactory> allFactories() {
    return typeFactories;
  }
}
