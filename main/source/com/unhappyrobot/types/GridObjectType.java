package com.unhappyrobot.types;

import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.math.GridPoint;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class GridObjectType {
  private String name;
  private int height;
  private int width;
  private int coins;
  private int experienceAward;
  private String atlasFilename;
  private String imageFilename;
  private boolean continuousPlacement;
  private boolean canShareSpace;
  private float noiseLevel;

  public abstract GridObject makeGridObject(GameGrid gameGrid);

  public abstract boolean canBeAt(GridObject gridObject);

  public String getName() {
    return name;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public int getCoins() {
    return coins;
  }

  public int getExperienceAward() {
    return experienceAward;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public String getAtlasFilename() {
    return atlasFilename;
  }

  public boolean continuousPlacement() {
//    return continuousPlacement;
    return true;
  }

  public int getZIndex() {
    return 0;
  }

  public float getNoiseLevel() {
    return noiseLevel;
  }

  public int getCoinsEarned() {
    return coins / 4;
  }

  public boolean canShareSpace(GridObject gridObject) {
    return canShareSpace;
  }

  protected boolean checkIfTouchingAnotherObject(GridObject gridObject) {
    Bounds2d belowObject = new Bounds2d(gridObject.getPosition().cpy().sub(0, 1), gridObject.getSize());

    GridPoint gridPoint = gridObject.getPosition().cpy();
    gridPoint.sub(0, 1);

    Set<GridObject> objectsBelow = GridPositionCache.instance().getObjectsAt(gridPoint, gridObject.getSize(), gridObject);
    return objectsBelow.size() != 0;
  }

  protected boolean checkForOverlap(GridObject gridObject) {
    Set<GridObject> objectsOverlapped = GridPositionCache.instance().getObjectsAt(gridObject.getPosition(), gridObject.getSize(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!gridObject.canShareSpace(object)) {
        return false;
      }
    }

    return objectsOverlapped.size() > 0;
  }

  @Override
  public String toString() {
    return String.format("%s@%s:%s", this.getClass().getName(), hashCode(), getName());
  }


}
