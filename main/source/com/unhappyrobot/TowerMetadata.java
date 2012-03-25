package com.unhappyrobot;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TowerMetadata {
  private String name;
  private DifficultyLevel difficulty;

  public void setName(String name) {
    this.name = name;
  }

  public void setDifficulty(DifficultyLevel difficulty) {
    this.difficulty = difficulty;
  }

  public String generateFilename() {
    return name.replaceAll(" ", "_") + ".json";
  }

  public DifficultyLevel getDifficulty() {
    return difficulty;
  }
}
