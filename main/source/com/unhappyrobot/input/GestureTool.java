package com.unhappyrobot.input;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public GestureListener newInstance() {
      return new PlacementTool();
    }
  }, NONE {
    @Override
    public GestureListener newInstance() {
      return null;
    }
  };

  public abstract GestureListener newInstance();
}
