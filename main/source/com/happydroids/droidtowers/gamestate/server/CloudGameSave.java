/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.NonInteractiveGameSave;
import com.happydroids.droidtowers.utils.GZIPUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class CloudGameSave extends TowerGameServiceObject {
  protected String blob;
  protected String image;
  protected Date syncedOn;
  protected List<FriendCloudGameSave> neighbors;

  public CloudGameSave() {

  }

  public CloudGameSave(String resourceUri) {
    setResourceUri(resourceUri);
  }

  public CloudGameSave(GameSave gameSave, FileHandle pngFile) {
    try {
      setResourceUri(gameSave.getCloudSaveUri());
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

  @JsonIgnore
  public GameSave getGameSave() {
    GameSave gameSave = NonInteractiveGameSave.readFile(new ByteArrayInputStream(blob.getBytes()), "cloudGameSave_" + hashCode());
    gameSave.setCloudSaveUri(getResourceUri());
    return gameSave;
  }

  public List<FriendCloudGameSave> getNeighbors() {
    return neighbors;
  }
}
