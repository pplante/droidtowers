package com.unhappyrobot.gamestate.server;

public class Consts {
  private Consts() {

  }

  public static final String HAPPYDROIDS_SERVER = "local.happydroids.com";
  public static final String HAPPYDROIDS_URI = "http://" + HAPPYDROIDS_SERVER;
  public static final String API_V1_REGISTER_DEVICE = HAPPYDROIDS_URI + "/api/v1/register-device/";
  public static final String API_V1_TEMPORARY_TOKEN_CREATE = HAPPYDROIDS_URI + "/api/v1/temporarytoken/";
  public static final String API_V1_GAMESAVE_LIST = HAPPYDROIDS_URI + "/api/v1/gamesave/";
}
