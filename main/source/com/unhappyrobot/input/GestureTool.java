package com.unhappyrobot.input;

public enum GestureTool {
  PLACEMENT() {
    @Override
    public ToolBase newInstance() {
      return new PlacementTool();
    }
  }, NONE {
    @Override
    public ToolBase newInstance() {
      return null;
    }
  };

  public abstract ToolBase newInstance();
}
