/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Function;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.HotelRoom;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.types.RoomType;
import org.apach3.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import static com.happydroids.droidtowers.ColorUtil.rgba;

@SuppressWarnings("ALL")
public enum Overlays {
  NOISE_LEVEL("#f20000") {
    @Override
    public Function<GridObject, Float> getMethod() {
      return null;
    }
  },
  CRIME_LEVEL("#8100c2") {
    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          return gridObject.getSurroundingCrimeLevel();
        }
      };
    }
  },
  POPULATION_LEVEL("#2000c2") {
    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          if (gridObject instanceof Room) {
            float populationMax = ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
            if (populationMax > 0f) {
              return ((Room) gridObject).getNumResidents() / populationMax;
            }
          }

          return null;
        }
      };
    }
  },
  EMPLOYMENT_LEVEL("#00c200") {
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
  DESIRABILITY_LEVEL("#a3da00") {
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
  },
  DIRT_LEVEL("#6f5506") {
    @Override
    public Function<GridObject, Float> getMethod() {
      return new Function<GridObject, Float>() {
        public Float apply(@Nullable GridObject gridObject) {
          if (gridObject instanceof CommercialSpace || gridObject instanceof HotelRoom) {
            return ((CommercialSpace) gridObject).getDirtLevel();
          }

          return null;
        }
      };
    }
  };

  private Overlays(Color color) {
    this.color = color;
  }

  private Overlays(String colorAsHex) {
    this(rgba(colorAsHex));
  }

  public Color getColor(float alpha) {
    this.color.a = alpha;
    return this.color;
  }

  private final Color color;

  public String toString() {
    return StringUtils.capitalize(name().substring(0, name().indexOf("_LEVEL")).toLowerCase());
  }

  public abstract Function<GridObject, Float> getMethod();
}
