package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.entities.GameGrid;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, GameGrid gameGrid) {
      return new PlacementTool(camera, gameGrid);
    }
  }, PICKER {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, GameGrid gameGrid) {
      return new PickerTool(camera, gameGrid);
    }
  };

  public abstract ToolBase newInstance(OrthographicCamera camera, GameGrid gameGrid);
}
