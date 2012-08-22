/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.happydroids.droidtowers.DifficultyLevel;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSaveMetadata {
  public int fileGeneration;
  public Date lastPlayed;
  public String cloudSaveUri;
  public String baseFilename;
  public String towerName;
  public DifficultyLevel difficultyLevel;

  public GameSaveMetadata() {
    baseFilename = GameSaveFactory.generateFilename();
  }

  public GameSaveMetadata(String towerName, DifficultyLevel difficultyLevel) {
    this();
    this.towerName = towerName;
    this.difficultyLevel = difficultyLevel;
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GameSaveMetadata)) {
      return false;
    }

    GameSaveMetadata metadata = (GameSaveMetadata) o;

    if (fileGeneration != metadata.fileGeneration) {
      return false;
    }
    if (baseFilename != null ? !baseFilename.equals(metadata.baseFilename) : metadata.baseFilename != null) {
      return false;
    }
    if (cloudSaveUri != null ? !cloudSaveUri.equals(metadata.cloudSaveUri) : metadata.cloudSaveUri != null) {
      return false;
    }
    if (difficultyLevel != metadata.difficultyLevel) {
      return false;
    }
    if (towerName != null ? !towerName.equals(metadata.towerName) : metadata.towerName != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = cloudSaveUri != null ? cloudSaveUri.hashCode() : 0;
    result = 31 * result + (baseFilename != null ? baseFilename.hashCode() : 0);
    result = 31 * result + (difficultyLevel != null ? difficultyLevel.hashCode() : 0);
    return result;
  }
}
