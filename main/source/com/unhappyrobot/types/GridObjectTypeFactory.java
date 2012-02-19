package com.unhappyrobot.types;

import com.badlogic.gdx.files.FileHandle;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
      List<T> newObjectTypes = mapper.readValue(fileHandle.reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, gridObjectTypeClass));

      if (objectTypes != null) {
        objectTypes.addAll(newObjectTypes);
      } else {
        objectTypes = newObjectTypes;
      }

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
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

  public static GridObjectTypeFactory getFactoryForType(Class<? extends GridObjectType> typeClass) {
    for (GridObjectTypeFactory typeFactory : typeFactories) {
      if (typeFactory.gridObjectTypeClass.equals(typeClass)) {
        return typeFactory;
      }
    }

    return null;
  }

  public Class<T> getObjectType() {
    return gridObjectTypeClass;
  }

  public T castToObjectType(Object o) {
    return gridObjectTypeClass.cast(o);
  }
}
