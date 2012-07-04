/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.gui.RoomPopOver;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.types.RoomType;

public class Room extends GridObject {
  private static BitmapFont labelFont;
  private static Texture roomDecals;
  private Sprite sprite;
  private Sprite decalSprite;

  private static final int UPDATE_FREQUENCY = 10000;
  private long lastUpdateTime;
  protected int currentResidency;
  private int populationRequired;

  private Avatar resident;

  protected GridObjectPopOver popOverLayer;
  protected String name;

  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    sprite = new Sprite(roomType.getTextureRegion());

    if (roomDecals == null) {
      roomDecals = new Texture(Gdx.files.internal("decals.png"));
    }

    decalSprite = new Sprite(roomDecals);

    desirability = 1f;
    name = NameGenerator.randomNameForGridObjectType(getGridObjectType());
    popOverLayer = makePopOver();
  }

  public GridObjectPopOver makePopOver() {
    return new RoomPopOver(this);
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
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    super.render(spriteBatch, renderTintColor);

    if (!connectedToTransport && !(gameGrid instanceof NeighborGameGrid)) {
      decalSprite.setPosition(sprite.getX(), sprite.getY());
      decalSprite.draw(spriteBatch);
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
      return MathUtils.clamp((desirability - getNoiseLevel() - surroundingNoiseLevel) - (getTransportModifier() * 0.33f) - ((getCrimeLevel() - surroundingCrimeLevel) * 0.33f), 0, 1f);
    }

    return 0f;
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

  protected void updatePopOverPosition() {
    if (popOverLayer.parent != null) {
      Vector3 vec = new Vector3(getWorldCenter().x + (worldSize.x / 2), getWorldCenter().y - popOverLayer.getPrefHeight() / 2, 1f);
      SceneManager.activeScene().getCamera().project(vec);
      popOverLayer.x = vec.x;
      popOverLayer.y = vec.y;
    }
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    updatePopOverPosition();
  }

  @Override
  public boolean touchDown(GridPoint gameGridPoint, Vector2 worldPoint, int pointer) {
    if (popOverLayer != null && !popOverLayer.visible) {
      HeadsUpDisplay.instance().setGridObjectPopOver(popOverLayer);
      return true;
    } else {
      HeadsUpDisplay.instance().setGridObjectPopOver(null);
    }

    return false;
  }

  public String getName() {
    return name != null ? name : gridObjectType.getName();
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
