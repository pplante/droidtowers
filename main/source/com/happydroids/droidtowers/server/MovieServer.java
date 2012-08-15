/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.badlogic.gdx.utils.Array;
import com.happydroids.utils.BackgroundTask;

import java.util.Iterator;

public class MovieServer {
  private static MovieServer _instance;
  private MovieCollection moviesList;
  private Iterator<Movie> playQueueIterator;
  private Array<Movie> playQueue;

  public static MovieServer instance() {
    if (_instance == null) {
      _instance = new MovieServer();
    }

    return _instance;
  }

  public static void dispose() {
    _instance = null;
  }

  public MovieServer() {
    playQueue = new Array<Movie>();
    playQueueIterator = playQueue.iterator();
    moviesList = new MovieCollection();

    new FetchMovieListTask().run();
  }

  public boolean hasMovies() {
    return playQueue.size > 0;
  }

  public Movie getMovie() {
    if (!playQueueIterator.hasNext()) {
      ((Array.ArrayIterator) playQueueIterator).reset();
    }

    return playQueueIterator.next();
  }

  public void addMovieToPlayQueue(Movie movie) {
    playQueue.add(movie);
  }

  public void removeMovieFromPlayQueue(Movie movie) {
    playQueue.removeValue(movie, false);
  }

  private class FetchMovieListTask extends BackgroundTask {
    @Override
    protected void execute() throws Exception {
      moviesList.fetch();

      for (Movie movie : moviesList.getObjects()) {
        movie.queueForDownload();
      }
    }
  }
}
