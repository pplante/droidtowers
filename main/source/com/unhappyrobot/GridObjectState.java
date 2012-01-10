package com.unhappyrobot;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.GridObjectPlacementState;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.GridObjectType;
import com.unhappyrobot.types.GridObjectTypeFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GridObjectState {
  private GridPoint position;
  private GridPoint size;
  private Class<? extends GridObjectType> typeClass;
  private String typeName;

  public GridObjectState() {

  }

  public GridObjectState(GridObject gridObject) {
    typeClass = gridObject.getGridObjectType().getClass();
    typeName = gridObject.getGridObjectType().getName();
    position = gridObject.getPosition();
    size = gridObject.getSize();
  }

  public Class<? extends GridObjectType> getTypeClass() {
    return typeClass;
  }

  public GridObject materialize(GameGrid gameGrid) {
    GridObjectTypeFactory factoryForType = GridObjectTypeFactory.getFactoryForType(typeClass);

    if (factoryForType != null) {
      GridObjectType objectType = factoryForType.findByName(typeName);

      if (objectType != null) {
        GridObject object = objectType.makeGridObject(gameGrid);

        if (object != null) {
          object.setPlacementState(GridObjectPlacementState.PLACED);
          object.setPosition(position.x, position.y);
          object.setSize(size.x, size.y);

          gameGrid.addObject(object);

          return object;
        }
      }
    } else {
      throw new RuntimeException("Looks like you forget to call getInstance() of the factory for type: " + typeClass.getName());
    }

    return null;
  }
}
