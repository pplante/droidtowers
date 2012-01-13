package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.Bounds2d;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class GridObjectType {
  private String name;
  private int height;
  private int width;
  private int coins;
  private int gold;
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

  public int getGold() {
    return gold;
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
    return continuousPlacement;
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

  public int getGoldEarned() {
    return gold / 10;
  }

  public boolean canShareSpace(GridObject gridObject) {
    return canShareSpace;
  }

  protected boolean checkIfTouchingAnotherObject(GridObject gridObject) {
    Bounds2d belowObject = new Bounds2d(gridObject.getPosition().toVector2().sub(0, 1), gridObject.getSize());

    List<GridObject> objectsBelow = gridObject.getGameGrid().getObjectsAt(belowObject, gridObject);
    return objectsBelow.size() != 0;

  }

  protected boolean checkForOverlap(GridObject gridObject) {
    List<GridObject> objectsOverlapped = gridObject.getGameGrid().getObjectsAt(gridObject.getBounds(), gridObject);
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
