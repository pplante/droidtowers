/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.entities.GameLayer;

import java.util.List;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers) {
      return new PlacementTool(camera, gameLayers);
    }
  }, PICKER {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers) {
      return new PickerTool(camera, gameLayers);
    }
  }, SELL {
    @Override
    public ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers) {
      return new SellTool(camera, gameLayers);
    }
  };

  public abstract ToolBase newInstance(OrthographicCamera camera, List<GameLayer> gameLayers);
}
