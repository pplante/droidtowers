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

  public int getCoinsEarned() {
    return coins / 4;
  }

  public int getGoldEarned() {
    return gold / 10;
  }

  public boolean canShareSpace(GridObject gridObject) {
    return canShareSpace;
  }

  protected boolean checkBelowForOtherObject(GridObject gridObject) {
    Bounds2d belowObject = new Bounds2d(gridObject.getPosition().cpy().sub(0, 1), gridObject.getSize());

    List<GridObject> position = gridObject.getGameGrid().getObjectsAt(belowObject);
    return position.size() == 0;
  }

  protected boolean checkBehindForOtherObject(GridObject gridObject) {
    return gridObject.getGameGrid().getObjectsAt(gridObject.getBounds()).size() > 1;
  }
}
