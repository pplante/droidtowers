/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
      return new PlacementTool(camera, gameLayers, gameGrid);
    }
  },
  PICKER {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
      return new PickerTool(camera, gameLayers, gameGrid);
    }
  },
  SELL {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
      return new SellTool(camera, gameLayers, gameGrid);
    }
  };

  public abstract ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid);
}
