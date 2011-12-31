package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class GridObjectType {
  private String name;
  private int height;
  private int width;
  private int coins;
  private int gold;
  private String atlasFilename;
  private String imageFilename;
  private boolean continuousPlacement;

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

  public String getImage() {
    return imageFilename;
  }

  public String getAtlas() {
    return atlasFilename;
  }

  public boolean continuousPlacement() {
    return continuousPlacement;
  }
}
