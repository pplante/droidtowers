/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.MovieTheaterPopOver;
import com.happydroids.droidtowers.server.Movie;
import com.happydroids.droidtowers.server.MovieServer;
import com.happydroids.droidtowers.tasks.MovieState;
import com.happydroids.droidtowers.types.CommercialType;

public class MovieTheater extends CommercialSpace {
  private Animation animation;
  private long nextShowTime;
  private float animationTime;
  private Movie movie;
  private boolean isPlaying;


  public MovieTheater(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);

    nextShowTime = System.currentTimeMillis();
    animationTime = 0f;
  }

  private void loadMovie() {
    if (MovieServer.instance().hasMovies()) {
      movie = MovieServer.instance().getMovie();
      movie.loadAssets(new Runnable() {
        @Override
        public void run() {
          if (movie.getState().equals(MovieState.Loaded)) {
            movie.incrementRefCount();
            animation = new Animation(1f / movie.getAtlasFps(), movie.getTextureAtlas().getRegions());
            animationTime = 0f;
            isPlaying = true;
            getSprite().setRegion(gridObjectType.getTextureAtlas().findRegion("4x1-movie-theater-on"));
          } else {
            endPlayback();
          }
        }
      });
    }
  }

  @Override
  public void render(SpriteBatch spriteBatch, SpriteCache spriteCache, Color renderTintColor) {
    if (animation != null) {
      if (isPlaying) {
        animationTime += Gdx.graphics.getDeltaTime();
        if (animationTime >= animation.animationDuration) {
          endPlayback();
        } else {
          Vector2 worldCenter = getWorldCenter();
          spriteBatch.draw(animation.getKeyFrame(animationTime, false), worldCenter.x - 53.5f * getGridScale(), worldCenter.y - 19 * getGridScale(), 107 * getGridScale(), 44 * getGridScale());
        }
      }
    }

    if (!isPlaying && movie == null && getEmploymentLevel() > 0f) {
      long millis = System.currentTimeMillis();
      if (nextShowTime <= millis) {
        loadMovie();
      }
    }

    super.render(spriteBatch, spriteCache, renderTintColor);
  }

  private void endPlayback() {
    if (movie != null) {
      movie.decrementRefCount();
      movie = null;
      animation = null;
    }

    getSprite().setRegion(gridObjectType.getTextureAtlas().findRegion("4x1-movie-theater"));
    nextShowTime = System.currentTimeMillis();// + (Random.randomInt(5, 15) * 1000);
    isPlaying = false;
  }

  @Override
  public boolean shouldUseSpriteCache() {
    return false;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new MovieTheaterPopOver(this);
  }

  public Movie getMovie() {
    return movie;
  }

  @Override
  public String getName() {
    if (movie != null) {
      if (isPlaying) {
        return "Now Playing: " + movie.getTitle();
      }

      return "Coming Soon: " + movie.getTitle();
    } else {
      return "Looking for Movies";
    }
  }
}
