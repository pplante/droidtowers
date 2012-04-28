/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.NonInteractiveGameSave;
import com.happydroids.droidtowers.utils.GZIPUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CloudGameSave extends TowerGameServiceObject {
  private String blob;
  private String image;
  private Date syncedOn;

  public CloudGameSave() {

  }

  public CloudGameSave(GameSave gameSave, FileHandle pngFile) {
    try {
      resourceUri = gameSave.getCloudSaveUri();
      blob = getObjectMapper().writeValueAsString(gameSave);
      image = GZIPUtils.compress(pngFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/gamesave/";
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public GameSave getGameSave() {
    return NonInteractiveGameSave.readFile(new ByteArrayInputStream(blob.getBytes()), "cloudGameSave_" + id);
  }
}
