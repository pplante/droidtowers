/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.ServiceRoomType;

public class ParkingGarageSpace extends ServiceRoom {
  public static final float VEHICLE_CHANGE_FREQUENCY = 15f;
  private static Array<TextureAtlas.AtlasRegion> vehicleTextures;
  private float timeUntilVehicleChange;
  private TextureAtlas.AtlasRegion activeVehicleTexture;

  public ParkingGarageSpace(ServiceRoomType serviceRoomType, GameGrid gameGrid) {
    super(serviceRoomType, gameGrid);

    connectedToTransport = true;

    if (vehicleTextures == null) {
      vehicleTextures = serviceRoomType.getTextureAtlas().findRegions("vehicle");
    }

    timeUntilVehicleChange = VEHICLE_CHANGE_FREQUENCY;
  }

  @Override
  protected boolean canEmployDroids() {
    return false;
  }

  @Override public boolean needsDroids() {
    return false;
  }

  @Override
  public boolean canEarnMoney() {
    return true;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return null;
  }

  @Override public void update(float deltaTime) {
    timeUntilVehicleChange -= deltaTime;
    if (timeUntilVehicleChange <= 0f) {
      timeUntilVehicleChange = VEHICLE_CHANGE_FREQUENCY + MathUtils.random(0f, 3.5f);
      activeVehicleTexture = null;

      int randomIndex = MathUtils.random(0, vehicleTextures.size);
      if (randomIndex < vehicleTextures.size) {
        activeVehicleTexture = vehicleTextures.get(randomIndex);
      }
    }
  }

  @Override public void render(SpriteBatch spriteBatch, SpriteCache spriteCache, Color renderTintColor) {
    super.render(spriteBatch, spriteCache, renderTintColor);

    if (activeVehicleTexture != null) {
      spriteBatch.setColor(Color.WHITE);
      spriteBatch.draw(activeVehicleTexture,
                              getWorldCenter().x - activeVehicleTexture.getRegionWidth() / 2,
                              getWorldCenter().y - activeVehicleTexture.getRegionHeight() / 2);

    }
  }

  @Override public boolean shouldUseSpriteCache() {
    return false;
  }
}
