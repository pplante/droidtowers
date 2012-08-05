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
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.MovieTheaterPopOver;
import com.happydroids.droidtowers.server.Movie;
import com.happydroids.droidtowers.server.MovieServer;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.utils.Random;

public class MovieTheater extends CommercialSpace {
  private TextureAtlas movieAtlas;
  private Animation animation;
  private long nextShowTime;
  private float animationTime;
  private Movie movie;
  private boolean isPlaying;


  public MovieTheater(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);

    nextShowTime = System.currentTimeMillis();
    animationTime = 0f;

    MovieServer.instance().afterFetchingMovies(new Runnable() {
      @Override
      public void run() {
        loadMovie();
      }
    });
  }

  private void loadMovie() {
    if (MovieServer.instance().hasMovies()) {
      movie = MovieServer.instance().getMovie();
      movie.afterDownload(new Runnable() {
        @Override
        public void run() {
          float fps = 1f / movie.getAtlasFps();
          movieAtlas = movie.getTextureAtlas();
          animation = new Animation(fps, movieAtlas.getRegions());
          nextShowTime = System.currentTimeMillis() + (Random.randomInt(5, 15) * 1000);
        }
      });
    }
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    if (animation != null) {

      if (isPlaying) {
        animationTime += Gdx.graphics.getDeltaTime();
        if (animationTime >= animation.animationDuration) {
          getSprite().setRegion(gridObjectType.getTextureAtlas().findRegion("4x1-movie-theater"));
          loadMovie();
          isPlaying = false;
        } else {
          Vector2 worldCenter = getWorldCenter();
          spriteBatch.draw(animation.getKeyFrame(animationTime, false), worldCenter.x - 53.5f * getGridScale(), worldCenter.y - 19 * getGridScale(), 107 * getGridScale(), 44 * getGridScale());
        }
      } else if (!isPlaying) {
        long millis = System.currentTimeMillis();
        if (nextShowTime <= millis) {
          animationTime = 0f;
          isPlaying = true;
          getSprite().setRegion(gridObjectType.getTextureAtlas().findRegion("4x1-movie-theater-on"));
        }
      }
    }

    super.render(spriteBatch, renderTintColor);
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
    return movie != null ? "Now Playing: " + movie.getTitle() : "Coming Soon";
  }
}
