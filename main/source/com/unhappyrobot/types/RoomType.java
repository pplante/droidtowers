package com.unhappyrobot.types;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Room;

public class RoomType extends GridObjectType {
  private String name;
  private boolean continuousPurchase;
  private int height;
  private int width;
  private String atlasFilename;
  private String imageFilename;

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getImage() {
    return imageFilename;
  }

  public void setImage(String imageFilename) {
    this.imageFilename = imageFilename;
  }

  public String getAtlas() {
    return atlasFilename;
  }

  public void setAtlas(String atlasFilename) {
    this.atlasFilename = atlasFilename;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public boolean continuousPlacement() {
    return continuousPurchase;
  }

  public void setContinuousPurchase(boolean continuousPurchase) {
    this.continuousPurchase = continuousPurchase;
  }

  public GridObject makeGridObject() {
    return new Room(this);
  }
}
