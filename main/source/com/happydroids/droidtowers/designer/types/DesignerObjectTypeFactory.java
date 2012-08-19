/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.types;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DesignerObjectTypeFactory {
  static DesignerObjectTypeFactory _instance;
  protected List<DesignerObjectType> objectTypes;

  public static DesignerObjectTypeFactory instance() {
    if (_instance == null) {
      _instance = new DesignerObjectTypeFactory();
    }

    return _instance;
  }

  public DesignerObjectTypeFactory() {
    objectTypes = Lists.newArrayList();
  }

  public List<DesignerObjectType> all() {
    return objectTypes;
  }

  @SuppressWarnings("unchecked")
  public DesignerObjectType findByName(String name) {
    for (DesignerObjectType type : objectTypes) {
      if (type.getName().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }

  public DesignerObjectType findById(String typeId) {
    for (DesignerObjectType type : objectTypes) {
      if (type.getTypeId().equalsIgnoreCase(typeId)) {
        return type;
      }
    }

    return null;
  }

  public List<DesignerObjectType> findByCategory(DesignerObjectCategory objectCategory) {
    List<DesignerObjectType> types = new ArrayList<DesignerObjectType>();

    for (DesignerObjectType objectType : objectTypes) {
      if (objectType.getCategory().equals(objectCategory)) {
        types.add(objectType);
      }
    }

    return types;
  }

  public void add(DesignerObjectType type) {
    for (DesignerObjectType objectType : objectTypes) {
      if (objectType.getTypeId().equalsIgnoreCase(type.getTypeId())) {
        return;
      }
    }

    objectTypes.add(type);
  }
}
