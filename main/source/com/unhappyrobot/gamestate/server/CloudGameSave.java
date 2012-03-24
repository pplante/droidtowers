package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.gamestate.GameSave;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.IOException;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties({"id"})
public class CloudGameSave {
  private int id;
  private String blob;
  private String resourceUri;
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
