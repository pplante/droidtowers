/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.RoomPopOver;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.RoomType;

import java.util.Set;

import static com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Room extends GridObject {
  private static BitmapFont labelFont;
  private Sprite sprite;

  private static final int UPDATE_FREQUENCY = 10000;
  private long lastUpdateTime;
  protected int currentResidency;
  private int populationRequired;

  private Avatar resident;
  private static AtlasRegion transportDisconnectedDecal;
  private static AtlasRegion cousinVinnieDecal;
  private Set<AtlasRegion> decalsToDraw;


  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    if (transportDisconnectedDecal == null) {
      transportDisconnectedDecal = getGridObjectType().getTextureAtlas().findRegion("transport-disconnected");
    }

    if (cousinVinnieDecal == null) {
      cousinVinnieDecal = getGridObjectType().getTextureAtlas().findRegion("cousin-vinnie");
    }

    sprite = new Sprite(roomType.getTextureRegion(getVariationId()));

    desirability = 1f;
    decalsToDraw = Sets.newHashSet();
  }

  public void updatePopulation() {
    currentResidency = 0;

    if (isConnectedToTransport()) {
      int maxPopulation = ((RoomType) getGridObjectType()).getPopulationMax();
      if (maxPopulation > 0) {
        currentResidency = (int) (maxPopulation * getDesirability());
      }
    }
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
        for (AtlasRegion region : decalsToDraw) {
          spriteBatch.draw(region, getWorldCenter().x - region.getRegionWidth() / 2, getWorldCenter().y - region.getRegionHeight() / 2);
        }
      } else {
        int decalsWidth = 0;
        for (AtlasRegion atlasRegion : decalsToDraw) {
          decalsWidth = atlasRegion.getRegionWidth();
        }

        float startX = getWorldCenter().x - decalsWidth;
        for (AtlasRegion region : decalsToDraw) {
          spriteBatch.draw(region, startX, getWorldCenter().y - region.getRegionHeight() / 2);
          startX += region.getRegionWidth();
        }
      }
    }
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);
    decalsToDraw.clear();

    if (loanFromCousinVinnie > 0) {
      decalsToDraw.add(cousinVinnieDecal);
    }

    if (!connectedToTransport && !(gameGrid instanceof NeighborGameGrid)) {
      decalsToDraw.add(transportDisconnectedDecal);
    }
  }

  public int getCurrentResidency() {
    return currentResidency;
  }

  @Override
  public int getCoinsEarned() {
    if (currentResidency > 0 && isConnectedToTransport()) {
      RoomType roomType = (RoomType) gridObjectType;
      return (int) ((roomType.getCoinsEarned() / roomType.getPopulationMax()) * getDesirability());
    }

    return 0;
  }

  @Override
  public float getNoiseLevel() {
    if (((RoomType) gridObjectType).getPopulationMax() > 0) {
      return super.getNoiseLevel() * (currentResidency / ((RoomType) gridObjectType).getPopulationMax());
    }

    return 0;
  }


  @Override
  public float getDesirability() {
    if (placed && connectedToTransport) {
      return MathUtils.clamp(Math.abs(desirability - getNoiseLevel() - surroundingNoiseLevel) - (getTransportModifier() * 0.33f) - (surroundingCrimeLevel * 0.75f), 0, 1f);
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

    return currentResidency / populationMax;
  }

  public Avatar getResident() {
    return resident;
  }
}
