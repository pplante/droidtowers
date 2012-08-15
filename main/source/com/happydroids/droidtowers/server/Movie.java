/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.eventbus.Subscribe;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.events.AssetLoadErrorEvent;
import com.happydroids.droidtowers.events.AssetLoadEvent;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.droidtowers.tasks.DownloadMovieAssetsTask;
import com.happydroids.droidtowers.tasks.MovieState;
import com.happydroids.server.HappyDroidServiceObject;
import org.apach3.commons.io.FilenameUtils;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC;

@JsonAutoDetect(fieldVisibility = PROTECTED_AND_PUBLIC)
public class Movie extends HappyDroidServiceObject {
  protected String title;
  protected int atlasFps;
  protected String atlasTxt;
  protected String atlasPng;
  protected String youtubeTrailerUrl;
  protected String ticketsPurchaseUrl;
  @JsonIgnore private FileHandle atlasTxtFile;
  @JsonIgnore private FileHandle atlasPngFile;
  @JsonIgnore private MovieState state;
  @JsonIgnore private int refs;
  @JsonIgnore private RunnableQueue postLoad;
  @JsonIgnore private TextureAtlas textureAtlas;

  public Movie() {
    state = MovieState.Queued;
    postLoad = new RunnableQueue();
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/movie/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  @Override
  protected int getCacheMaxAge() {
    return HappyDroidConsts.ONE_DAY;
  }

  @Override
  protected boolean isCachingAllowed() {
    return true;
  }

  public void loadAssets(final Runnable runnable) {
    postLoad.push(runnable);
    TowerAssetManager.assetManager().events().register(this);
    TowerAssetManager.assetManager().load(atlasTxtFile.file().getAbsolutePath(), TextureAtlas.class);
  }

  public void queueForDownload() {
    if (state.equals(MovieState.Loaded) || state.equals(MovieState.Downloading)) {
      return;
    }

    final FileHandle movieStorageRoot = GameSaveFactory.getStorageRoot().child("movies/");
    if (!movieStorageRoot.exists()) {
      movieStorageRoot.mkdirs();
    }

    setAtlasTxtFile(movieStorageRoot.child(FilenameUtils.getName(atlasTxt)));
    setAtlasPngFile(movieStorageRoot.child(FilenameUtils.getName(atlasPng)));

    new DownloadMovieAssetsTask(this).run();
  }

  public TextureAtlas getTextureAtlas() {
    if (textureAtlas == null) {
      textureAtlas = TowerAssetManager.textureAtlas(getAtlasTxtFile().file().getAbsolutePath());
    }

    return textureAtlas;
  }

  public int getAtlasFps() {
    return atlasFps;
  }

  public String getYoutubeTrailerUrl() {
    return youtubeTrailerUrl;
  }

  public String getTicketsPurchaseUrl() {
    return ticketsPurchaseUrl;
  }

  public String getTitle() {
    return title;
  }

  public void decrementRefCount() {
    refs -= 1;

    if (refs == 0) {
      textureAtlas.dispose();
      textureAtlas = null;
    }
  }

  public void incrementRefCount() {
    refs += 1;
  }

  public FileHandle getAtlasTxtFile() {
    return atlasTxtFile;
  }

  public void setAtlasTxtFile(FileHandle atlasTxtFile) {
    this.atlasTxtFile = atlasTxtFile;
  }

  public FileHandle getAtlasPngFile() {
    return atlasPngFile;
  }

  public void setAtlasPngFile(FileHandle atlasPngFile) {
    this.atlasPngFile = atlasPngFile;
  }

  public String getAtlasPngUrl() {
    return HappyDroidConsts.HAPPYDROIDS_URI + atlasPng;
  }

  public String getAtlasTxtUrl() {
    return HappyDroidConsts.HAPPYDROIDS_URI + atlasTxt;
  }

  public void setState(MovieState state) {
    this.state = state;
  }

  public MovieState getState() {
    return state;
  }

  @Subscribe
  public void AssetManager_onAssetLoad(AssetLoadEvent event) {
    if (!event.getFileName().contains(atlasPngFile.name()) && !event.getFileName().contains(atlasTxtFile.name())) {
      return;
    }

    TowerAssetManager.events().unregister(this);
    setState(event instanceof AssetLoadErrorEvent ? MovieState.Failed : MovieState.Loaded);

    if (getState().equals(MovieState.Failed)) {
      if (atlasPngFile.exists()) {
        atlasPngFile.delete();
      }

      if (atlasTxtFile.exists()) {
        atlasTxtFile.delete();
      }

      MovieServer.instance().removeMovieFromPlayQueue(this);
    }

    postLoad.runAll();
  }
}
