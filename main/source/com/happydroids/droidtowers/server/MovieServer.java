/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.utils.BackgroundTask;

public class MovieServer {
  private static MovieServer _instance;
  private MovieCollection moviesList;
  private RunnableQueue afterFetchRunnables;

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
    Movie movie = moviesList.getObjects().get(0);
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
