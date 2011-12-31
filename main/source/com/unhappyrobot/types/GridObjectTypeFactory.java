package com.unhappyrobot.types;

import com.badlogic.gdx.files.FileHandle;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GridObjectTypeFactory<T> {
  protected List<T> objectTypes;
  private Class<T> gridObjectTypeClass;

  public GridObjectTypeFactory(Class<T> gridObjectTypeClass) {
    this.gridObjectTypeClass = gridObjectTypeClass;
  }

  protected boolean parseTypesFile(FileHandle fileHandle) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      objectTypes = mapper.readValue(fileHandle.reader(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, gridObjectTypeClass));

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  public List<T> all() {
    return objectTypes;
  }
}
