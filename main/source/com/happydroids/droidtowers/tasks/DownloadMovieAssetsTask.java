/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tasks;

import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.server.Movie;
import com.happydroids.droidtowers.server.MovieServer;
import com.happydroids.droidtowers.utils.FileUtils;
import com.happydroids.droidtowers.utils.StringUtils;
import com.happydroids.utils.BackgroundTask;

import static com.happydroids.HappyDroidConsts.ONE_DAY;

public class DownloadMovieAssetsTask extends BackgroundTask {
  private Movie movie;
  private final FileHandle atlasTxtFile;
  private final FileHandle atlasPngFile;

  public DownloadMovieAssetsTask(Movie movie) {
    this.movie = movie;
    atlasTxtFile = this.movie.getAtlasTxtFile();
    atlasPngFile = this.movie.getAtlasPngFile();
  }

  @Override
  protected void execute() throws Exception {
    if (!atlasTxtFile.exists() || !atlasPngFile.exists()) {
      FileUtils.downloadAndCacheFile(movie.getAtlasTxtUrl(), ONE_DAY * 90, atlasTxtFile);
      FileUtils.downloadAndCacheFile(movie.getAtlasPngUrl(), ONE_DAY * 90, atlasPngFile);

      String content = movie.getAtlasTxtFile().readString();
      String[] lines = content.split("\n");
      lines[0] = atlasPngFile.name();
      atlasTxtFile.writeString(StringUtils.join(lines, "\n"), false);
    }
  }

  @Override
  public synchronized void afterExecute() {
    if (atlasTxtFile.length() > 0 && atlasPngFile.length() > 0) {
      MovieServer.instance().addMovieToPlayQueue(movie);
    } else {
      movie.setState(MovieState.Failed);
    }
  }
}
