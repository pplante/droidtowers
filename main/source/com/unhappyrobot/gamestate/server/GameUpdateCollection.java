package com.unhappyrobot.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.unhappyrobot.TowerConsts;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameUpdateCollection extends HappyDroidServiceCollection<GameUpdate> {
  public GameUpdateCollection() {
    super(GameUpdate.class);
  }

  @Override
  public String getBaseResourceUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/gameupdate/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
