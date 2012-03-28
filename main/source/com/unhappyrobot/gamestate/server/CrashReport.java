package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerGame;
import com.unhappyrobot.gamestate.GameSave;
import com.unhappyrobot.scenes.TowerScene;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@SuppressWarnings("ALL")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CrashReport extends HappyDroidServiceObject {
  private final String name;
  private final String deviceOsVersion;
  private final String deviceType;
  private final String message;
  private final StackTraceElement[] stackTrace;
  private GameSave gameState;

  public CrashReport(Throwable error) {
    deviceType = HappyDroidService.getDeviceType();
    deviceOsVersion = HappyDroidService.getDeviceOSVersion();
    name = error.getClass().getCanonicalName();
    message = error.getMessage();
    stackTrace = error.getStackTrace();
    gameState = null;

    if (TowerGame.getActiveScene() instanceof TowerScene) {
      ((TowerScene) TowerGame.getActiveScene()).getCurrentGameSave().update();
      gameState = ((TowerScene) TowerGame.getActiveScene()).getCurrentGameSave();
    }
  }

  @Override
  protected String getResourceBaseUri() {
    return Consts.HAPPYDROIDS_URI + "/api/v1/crashreport/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
