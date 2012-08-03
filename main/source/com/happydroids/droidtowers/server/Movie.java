/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.RunnableQueue;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.server.HappyDroidServiceObject;
import com.happydroids.utils.BackgroundTask;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;

public class Movie extends HappyDroidServiceObject {
  protected String title;
  protected int atlasFps;
  protected String atlasTxt;
  protected String atlasPng;
  protected String youtubeTrailerUrl;
  protected String ticketsPurchaseUrl;
  private RunnableQueue afterDownloadRunnables;
  private boolean downloaded;
  private FileHandle atlasTxtFile;
  private FileHandle atlasPngFile;
  private boolean downloading;


  public Movie() {
    afterDownloadRunnables = new RunnableQueue();
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/movie/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  public void afterDownload(Runnable runnable) {
    if (!isDownloaded()) {
      afterDownloadRunnables.push(runnable);
    } else {
      runnable.run();
    }
  }

  public boolean isDownloaded() {
    return downloaded;
  }

  public void queueForDownload() {
    if (downloaded || downloading) return;

    downloading = true;

    final FileHandle movieStorageRoot = GameSaveFactory.getStorageRoot().child("movies/");
    if (!movieStorageRoot.exists()) {
      movieStorageRoot.mkdirs();
    }

    atlasTxtFile = movieStorageRoot.child(FilenameUtils.getName(atlasTxt));
    atlasPngFile = movieStorageRoot.child(FilenameUtils.getName(atlasPng));

    new BackgroundTask() {
      @Override
      protected void execute() throws Exception {
        HttpResponse atlasTxtResp = TowerGameService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + atlasTxt, null);
        if (atlasTxtResp != null && atlasTxtResp.getStatusLine() != null && atlasTxtResp.getStatusLine().getStatusCode() == 200) {
          atlasTxtFile.write(atlasTxtResp.getEntity().getContent(), false);
        }

        HttpResponse atlasPngResp = TowerGameService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + atlasPng, null);
        if (atlasPngResp != null && atlasPngResp.getStatusLine() != null && atlasPngResp.getStatusLine().getStatusCode() == 200) {
          atlasPngFile.write(atlasPngResp.getEntity().getContent(), false);
        }
      }

      @Override
      public synchronized void afterExecute() {
        if (atlasTxtFile.exists() && atlasPngFile.exists()) {
          TextureAtlasLoader.TextureAtlasParameter parameter = new TextureAtlasLoader.TextureAtlasParameter();
          parameter.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
            @Override
            public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
              downloaded = true;
              downloading = false;
              afterDownloadRunnables.runAll();
            }
          };

          TowerAssetManager.assetManager().load(atlasTxtFile.file().getAbsolutePath(), TextureAtlas.class, parameter);
        }
      }
    }.run();
  }

  public TextureAtlas getTextureAtlas() {
    return TowerAssetManager.textureAtlas(atlasTxtFile.file().getAbsolutePath());
  }

  public int getAtlasFps() {
    return atlasFps;
  }
}
