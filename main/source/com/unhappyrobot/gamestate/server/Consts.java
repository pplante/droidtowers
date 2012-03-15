package com.unhappyrobot.gamestate.server;

public class Consts {
  private Consts() {

  }

  public static final String HAPPYDROIDS_SERVER = "http://local.happydroids.com";
  public static final String API_V1_REGISTER_DEVICE = HAPPYDROIDS_SERVER + "/api/v1/register-device/";
  public static final String API_V1_TEMPORARY_TOKEN_CREATE = HAPPYDROIDS_SERVER + "/api/v1/temporarytoken/";
  public static final String API_V1_GAMESAVE_LIST = HAPPYDROIDS_SERVER + "/api/v1/gamesave/";
}
