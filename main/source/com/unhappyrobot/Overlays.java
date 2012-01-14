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
      return "Noise";
    }
  },
  POPULATION_LEVEL {
    @Override
    public String toString() {
      return "Population";
    }
  },
  EMPLOYMENT_LEVEL {
    @Override
    public String toString() {
      return "Employment";
    }
  }, DESIRABLITY_LEVEL {
    @Override
    public String toString() {
      return "Desirability";
    }
  };

  public abstract String toString();
}
