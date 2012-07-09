/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.RoomPopOver;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.RoomType;

import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Room extends GridObject {
  private static BitmapFont labelFont;
  public static final String DECAL_COUSIN_VINNIE = "cousin-vinnie";
  public static final String DECAL_TRANSPORT_DISCONNECTED = "transport-disconnected";
  private Sprite sprite;

  private static final int UPDATE_FREQUENCY = 10000;
  private long lastUpdateTime;
  private int populationRequired;

  private Avatar resident;
  private Set<Avatar> residents;
  protected Set<String> decalsToDraw;
  private static Map<String, TextureRegion> availableDecals;


  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    if (availableDecals == null) {
      availableDecals = Maps.newHashMap();

      for (AtlasRegion region : TowerAssetManager.textureAtlas("rooms/decals.txt").getRegions()) {
        availableDecals.put(region.name, region);
      }
    }

    sprite = new Sprite(roomType.getTextureRegion(getVariationId()));

    desirability = 1f;
    decalsToDraw = Sets.newHashSet();
    residents = Sets.newHashSet();
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void updateSprite() {
    sprite.setRegion(gridObjectType.getTextureRegion(getVariationId()));
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    super.render(spriteBatch, renderTintColor);


    if (!decalsToDraw.isEmpty()) {
      spriteBatch.setColor(Color.WHITE);

      if (decalsToDraw.size() == 1) {
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          spriteBatch.draw(region, getWorldCenter().x - region.getRegionWidth() / 2, getWorldCenter().y - region.getRegionHeight() / 2);
        }
      } else {
        int decalsWidth = 0;
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          decalsWidth = region.getRegionWidth();
        }

        float startX = getWorldCenter().x - decalsWidth;
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          spriteBatch.draw(region, startX, getWorldCenter().y - region.getRegionHeight() / 2);
          startX += region.getRegionWidth();
        }
      }
    }
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    if (loanFromCousinVinnie > 0) {
      decalsToDraw.add(DECAL_COUSIN_VINNIE);
    } else {
      decalsToDraw.remove(DECAL_COUSIN_VINNIE);
    }

    if (!connectedToTransport) {
      decalsToDraw.add(DECAL_TRANSPORT_DISCONNECTED);
    } else {
      decalsToDraw.remove(DECAL_TRANSPORT_DISCONNECTED);
    }
  }

  public int getNumResidents() {
    return residents.size();
  }

  @Override
  public int getCoinsEarned() {
    if (getNumResidents() > 0 && isConnectedToTransport()) {
      RoomType roomType = (RoomType) gridObjectType;
      return roomType.getCoinsEarned() * getNumResidents();
    }

    return 0;
  }

  @Override
  public float getNoiseLevel() {
    if (((RoomType) gridObjectType).getPopulationMax() > 0) {
      return super.getNoiseLevel() * (getNumResidents() / ((RoomType) gridObjectType).getPopulationMax());
    }

    return 0;
  }


  @Override
  public float getDesirability() {
    if (placed && connectedToTransport) {
      float value = 1f;
      value -= getSurroundingNoiseLevel();
      value -= getTransportModifier();
      value -= getSurroundingCrimeLevel();
      return MathUtils.clamp(value, 0, 1f);
    }

    return 0f;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new RoomPopOver(this);
  }

  @Override
  protected boolean hasPopOver() {
    return true;
  }

  private float getTransportModifier() {
    float minDist = Float.MAX_VALUE;
    for (GridPoint gridPoint : getGridPointsTouched()) {
      minDist = Math.min(gameGrid.positionCache().getPosition(gridPoint).normalizedDistanceFromTransit, minDist);
    }
    return minDist;
  }

  public boolean hasResident() {
    return resident != null;
  }

  public void setResident(Avatar avatar) {
    resident = avatar;
  }

  public float getResidencyLevel() {
    int populationMax = ((RoomType) gridObjectType).getPopulationMax();
    if (populationMax == 0) {
      return 0;
    }

    return residents.size() / populationMax;
  }

  public Set<Avatar> getResidents() {
    return residents;
  }

  @Override
  public String toString() {
    return "Room{" +
                   "currentResidency=" + getNumResidents() +
                   ", populationRequired=" + populationRequired +
                   ", residents=" + residents +
                   '}';
  }

  public void addResident(Avatar avatar) {
    residents.add(avatar);
  }

  public boolean hasResidents() {
    return !residents.isEmpty();
  }

  public int getNumSupportedResidents() {
    return (int) (((RoomType) gridObjectType).getPopulationMax() * getDesirability());
  }
}
