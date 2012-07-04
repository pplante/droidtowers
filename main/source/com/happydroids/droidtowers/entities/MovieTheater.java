/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
  private long avengersTime;
  private float animationTime;

  public MovieTheater(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);

    avengers = TowerAssetManager.textureAtlas("movies/avengers.txt");
    avengersMovie = new Animation(0.33f, avengers.getRegions());
    avengersTime = System.currentTimeMillis();
    animationTime = 0f;
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    animationTime += Gdx.graphics.getDeltaTime();
    long currentMillis = System.currentTimeMillis();
    if (avengersTime <= currentMillis) {
      avengersTime = currentMillis + 140000;
      animationTime = 0f;
    }

    Vector2 worldCenter = getWorldCenter();
    spriteBatch.draw(avengersMovie.getKeyFrame(animationTime, true), worldCenter.x - 53.5f * getGridScale(), worldCenter.y - 19 * getGridScale(), 107 * getGridScale(), 44 * getGridScale());

    super.render(spriteBatch, renderTintColor);
  }
/*
  @Override
  public boolean touchDown(Vector2 gameGridPoint) {
    DroidTowersGame.getBrowserUtil().launchWebBrowser("http://www.tkqlhce.com/da74cy63y5LRTPTMPTLNMRVVTUR");
    return true;
  }
 */
}
