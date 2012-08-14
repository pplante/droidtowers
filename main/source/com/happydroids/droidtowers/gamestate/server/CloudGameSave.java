/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import org.apach3.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class CloudGameSave extends TowerGameServiceObject {
  protected String blob;
  protected String image;
  protected Date syncedOn;
  protected int fileGeneration;
  private List<String> neighbors;
  private FriendCloudGameSaveCollection neighborGameSaves;
  private boolean upgradedGameSave;


  public CloudGameSave() {
    neighborGameSaves = new FriendCloudGameSaveCollection();
  }

  public CloudGameSave(String resourceUri) {
    this();

    setResourceUri(resourceUri);
  }

  public CloudGameSave(GameSave gameSave, FileHandle pngFile) {
    this();

    try {
      fileGeneration = gameSave.getFileGeneration();
      setResourceUri(gameSave.getCloudSaveUri());
      blob = getObjectMapper().writeValueAsString(gameSave);
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
    GameSave gameSave = GameSaveFactory.readFile(new ByteArrayInputStream(getBlob().getBytes()), "cloudGameSave_" + hashCode());
    gameSave.setCloudSaveUri(getResourceUri());
    return gameSave;
  }

  @JsonIgnore
  public FriendCloudGameSaveCollection getNeighborGameSaves() {
    return neighborGameSaves;
  }

  public List<String> getNeighbors() {
    List<String> neighborUris = Lists.newArrayList();

    if (neighborGameSaves != null) {
      for (FriendCloudGameSave gameSave : neighborGameSaves.getObjects()) {
        neighborUris.add(URI.create(gameSave.getResourceUri()).getPath());
      }
    }

    return neighborUris;
  }

  public void setNeighbors(List<String> neighborUris) {
    neighborGameSaves.clear();

    for (String neighborUri : neighborUris) {
      neighborGameSaves.add(new FriendCloudGameSave(neighborUri));
    }
  }

  public GameSave getGameSaveMetadata() {
    return GameSaveFactory.readMetadata(new ByteArrayInputStream(getBlob().getBytes()));
  }

  public int getFileGeneration() {
    return fileGeneration;
  }

  public String getBlob() {
    if (!upgradedGameSave) {
      upgradedGameSave = true;
      try {
        blob = getObjectMapper().writeValueAsString(GameSaveFactory.upgradeGameSave(new ByteArrayInputStream(blob.getBytes()), null));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return blob;
  }

  @JsonIgnore
  public boolean hasNeighbors() {
    return ((neighbors != null) && !neighbors.isEmpty()) || ((neighborGameSaves != null) && !neighborGameSaves.isEmpty());
  }

  @JsonIgnore
  public int numNeighbors() {
    return getNeighbors().size();
  }

  public byte[] getImage() {
    return Base64.decodeBase64(image);
  }

  public void updateImage(FileHandle pngFile) {
    image = Base64.encodeBase64URLSafeString(pngFile.readBytes());
  }
}
