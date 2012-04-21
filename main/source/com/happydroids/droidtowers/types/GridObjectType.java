/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementReward;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPositionCache;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public abstract class GridObjectType {
  private static final String TAG = GridObjectType.class.getSimpleName();

  protected String id;
  protected String name;
  protected int height;
  protected int width;
  protected int coins;
  protected int experienceAward;
  protected String atlasFilename;
  protected String imageFilename;
  protected boolean canShareSpace;
  protected float noiseLevel;

  protected ProviderType provides;
  private static WeakHashMap<String, TextureAtlas> atlases;
  private TextureAtlas textureAtlas;
  private AchievementReward achievementLock;

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

  public int getZIndex() {
    return 0;
  }

  public float getNoiseLevel() {
    return noiseLevel;
  }

  public int getCoinsEarned() {
    return Math.round(coins / 1000);
  }

  public int getUpkeepCost() {
    return Math.round(coins / 1000);
  }

  public boolean canShareSpace(GridObject gridObject) {
    return canShareSpace;
  }

  protected boolean checkIfTouchingAnotherObject(GridObject gridObject) {
    GridPoint gridPointBelow = gridObject.getPosition().cpy();
    gridPointBelow.sub(0, 1);

    GridPoint gridPointAbove = gridObject.getPosition().cpy();
    gridPointAbove.add(0, 1);

    Set<GridObject> objectsBelow = GridPositionCache.instance().getObjectsAt(gridPointBelow, gridObject.getSize(), gridObject);
    Set<GridObject> objectsAbove = GridPositionCache.instance().getObjectsAt(gridPointAbove, gridObject.getSize(), gridObject);

    return objectsBelow.size() != 0 || objectsAbove.size() != 0;
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
    return "GridObjectType{" +
                   "name='" + name + '\'' +
                   ", height=" + height +
                   ", width=" + width +
                   '}';
  }

  public TextureRegion getTextureRegion() {
    if (atlasFilename != null) {
      TextureAtlas objectAtlas = getTextureAtlas();

      return objectAtlas.findRegion(imageFilename);
    }

    return null;
  }

  @JsonIgnore
  public TextureAtlas getTextureAtlas() {
    if (atlases == null) {
      atlases = new WeakHashMap<String, TextureAtlas>();
    }

    TextureAtlas objectAtlas = atlases.get(atlasFilename);
    if (objectAtlas == null) {
      objectAtlas = TowerAssetManager.textureAtlas(atlasFilename);
      atlases.put(atlasFilename, objectAtlas);
    }
    return objectAtlas;
  }

  public boolean isLocked() {
    return achievementLock != null;
  }

  public String getId() {
    return id;
  }

  public boolean provides(ProviderType... thingProviderTypes) {
    if (provides != null) {
      HashSet<ProviderType> typeHashSet = Sets.newHashSet(provides);

      for (ProviderType otherType : thingProviderTypes) {
        if (otherType.matches(typeHashSet)) {
          return true;
        }
      }
    }

    return false;
  }

  public void addLock(AchievementReward reward) {
    if (!isLocked()) {
      achievementLock = reward;
      Gdx.app.debug(TAG, name + " locked by " + reward);
    } else {
      Gdx.app.debug(TAG, name + " is already locked by " + reward);
    }
  }

  public void removeLock() {
    if (achievementLock != null) {
      achievementLock = null;
      Gdx.app.debug(TAG, name + " unlocked.");
    }
  }
}
