package com.unhappyrobot.input;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public GestureListener newInstance() {
      return new PlacementTool();
    }
  };

  public abstract GestureListener newInstance();
}
