/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.CommercialType;

public class MovieTheater extends CommercialSpace {
  private TextureAtlas avengers;
  private Animation avengersMovie;
  private float avengersTime;

  public MovieTheater(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);

    avengers = TowerAssetManager.textureAtlas("movies/avengers.txt");
    avengersMovie = new Animation(0.3f, avengers.getRegions());
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    avengersTime += Gdx.graphics.getDeltaTime();
    if (avengersTime >= 140f) {
      avengersTime = 0f;
    }

    Vector2 worldCenter = getWorldCenter();
    spriteBatch.draw(avengersMovie.getKeyFrame(avengersTime, true), worldCenter.x - 53.5f, worldCenter.y - 19, 107, 44);

    super.render(spriteBatch);
  }
}
