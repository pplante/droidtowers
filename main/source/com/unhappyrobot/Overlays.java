package com.unhappyrobot;

public enum Overlays {
  NONE {
    @Override
    public String toString() {
      return "None";
    }
  },
  NOISE_LEVEL {
    @Override
    public String toString() {
      return "Noise Level";
    }
  },
  POPULATION_LEVEL {
    @Override
    public String toString() {
      return "Population Level";
    }
  },
  EMPLOYMENT_LEVEL {
    @Override
    public String toString() {
      return "Employment Level";
    }
  };

  public abstract String toString();
}
