package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.gamestate.GameSave;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.io.IOException;
import java.util.Date;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CloudGameSave extends HappyDroidServiceObject {
  private String blob;
  private Date syncedOn;

  @Override
  protected String getResourceBaseUri() {
    return Consts.HAPPYDROIDS_URI + "/api/v1/gamesave/";
  }

  public CloudGameSave() {

  }

  public CloudGameSave(GameSave gameSave) {
    try {
      resourceUri = gameSave.getCloudSaveUri();
      blob = GameSave.getObjectMapper().writeValueAsString(gameSave);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
