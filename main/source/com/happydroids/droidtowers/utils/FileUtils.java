/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apach3.http.HttpResponse;

import java.io.IOException;

public class FileUtils extends org.apach3.commons.io.FileUtils {
  public static void downloadAndCacheFile(final String urlString, final int cacheMaxAge, final FileHandle fileHandle) throws IOException {
    HttpResponse atlasPngResp = TowerGameService.instance().makeGetRequest(urlString, null, true, cacheMaxAge);
    if (atlasPngResp != null && atlasPngResp.getStatusLine() != null && atlasPngResp.getStatusLine()
                                                                                .getStatusCode() == 200) {
      fileHandle.write(atlasPngResp.getEntity().getContent(), false);
    }
  }
}
