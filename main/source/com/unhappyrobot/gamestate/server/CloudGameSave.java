package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.gamestate.GameSave;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.io.IOException;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CloudGameSave {
  private int id;
  private String blob;
  private String resource_uri;
  private Date synced_on;

  public CloudGameSave() {

  }

  public CloudGameSave(GameSave gameSave) {
    try {
      blob = GameSave.getObjectMapper().writeValueAsString(gameSave);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
