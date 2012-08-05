/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.google.common.collect.Iterators;
import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.utils.BackgroundTask;

import java.util.Iterator;

public class MovieServer {
  private static MovieServer _instance;
  private MovieCollection moviesList;
  private RunnableQueue afterFetchRunnables;
  private Iterator<Movie> moviesIterator;

  public static MovieServer instance() {
    if (_instance == null) {
      _instance = new MovieServer();
    }

    return _instance;
  }

  public MovieServer() {
    afterFetchRunnables = new RunnableQueue();
    moviesList = new MovieCollection();
    new BackgroundTask() {
      @Override
      protected void execute() throws Exception {
        moviesList.fetch();

        for (Movie movie : moviesList.getObjects()) {
          movie.queueForDownload();
        }
      }

      @Override
      public synchronized void afterExecute() {
        afterFetchRunnables.runAll();
      }
    }.run();
  }

  public boolean hasMovies() {
    return moviesList != null && !moviesList.isEmpty();
  }

  public Movie getMovie() {
    if (moviesIterator == null) {
      moviesIterator = Iterators.cycle(moviesList.getObjects());
    }
    Movie movie = moviesIterator.next();
    movie.queueForDownload();
    return movie;
  }

  public void afterFetchingMovies(Runnable runnable) {
    if (!hasMovies()) {
      afterFetchRunnables.push(runnable);
    } else {
      runnable.run();
    }
  }
}
