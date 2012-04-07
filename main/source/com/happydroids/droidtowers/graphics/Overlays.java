/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Function;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.types.RoomType;

import javax.annotation.Nullable;

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

    @Override
    public Function<GridObject, Float> getMethod() {
      return null;
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

    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          if (gridObject instanceof Room) {
            float populationMax = ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
            if (populationMax > 0f) {
              return ((Room) gridObject).getCurrentResidency() / populationMax;
            }
          }

          return null;
        }
      };
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

    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          if (gridObject instanceof CommercialSpace) {
            float jobsProvided = ((CommercialType) gridObject.getGridObjectType()).getJobsProvided();
            if (jobsProvided > 0f) {
              return ((CommercialSpace) gridObject).getJobsFilled() / jobsProvided;
            }
          }

          return null;
        }
      };
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

    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          if (gridObject != null) {
            return gridObject.getDesirability();
          }

          return null;
        }
      };
    }
  };

  public abstract String toString();

  public abstract Color getColor(float alpha);

  public abstract Function<GridObject, Float> getMethod();
}
