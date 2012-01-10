package com.unhappyrobot;

import com.google.common.collect.Lists;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.GridObjectPlacementState;
import com.unhappyrobot.entities.Player;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "class")
public class GameState {
  protected Player player;
  protected List<GridObjectState> gridObjects;

  public GameState() {

  }

  public GameState(GameGrid gameGrid, Player player) {
    this.player = player;

    gridObjects = Lists.newArrayList();

    for (GridObject gridObject : gameGrid.getObjects()) {
      if (gridObject.getPlacementState().equals(GridObjectPlacementState.PLACED)) {
        gridObjects.add(new GridObjectState(gridObject));
      }
    }
  }
}
