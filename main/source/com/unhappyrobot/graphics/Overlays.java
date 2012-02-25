package com.unhappyrobot.graphics;

import com.badlogic.gdx.graphics.Color;

public enum Overlays {
  NOISE_LEVEL {
    @Override
    public Color getColor(float alpha) {
      return new Color(1, 0, 0, alpha);
    }

    @Override
    public String toString() {
      return "Noise";
    }
  },
  POPULATION_LEVEL {
    @Override
    public Color getColor(float alpha) {
      return new Color(0, 0, 1, alpha);
    }

    @Override
    public String toString() {
      return "Population";
    }
  },
  EMPLOYMENT_LEVEL {
    @Override
    public Color getColor(float alpha) {
      return new Color(0, 1, 0, alpha);
    }

    @Override
    public String toString() {
      return "Employment";
    }
  },
  DESIRABILITY_LEVEL {
    @Override
    public Color getColor(float alpha) {
      return new Color(0.5f, 0.25f, 0.6f, alpha);
    }

    @Override
    public String toString() {
      return "Desirability";
    }
  };

  public abstract String toString();

  public abstract Color getColor(float alpha);
}
