/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameUpdate extends HappyDroidServiceObject {
  public Date releasedOn;
  public String gitSha;
  public String version;
  public int versionCode;
  public String notes;
  public boolean active;

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/gameupdate/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  @Override
  public String toString() {
    return "GameUpdate{" +
                   "gitSha='" + gitSha + '\'' +
                   ", releasedOn=" + releasedOn +
                   ", version='" + version + '\'' +
                   '}';
  }

  public String getGitSHA() {
    return gitSha;
  }
}
