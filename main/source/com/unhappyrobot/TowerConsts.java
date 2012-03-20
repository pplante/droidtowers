package com.unhappyrobot;

public class TowerConsts {
  public static final float ONE_MEGABYTE = 1048576.0f;

  private TowerConsts() {

  }

  public static final int GAME_WORLD_PADDING = 200;

  public static final int GAME_GRID_EXPAND_SIZE = 5;

  public static final float HUD_UPDATE_FREQUENCY = 1f;

  public static final float ROOM_UPDATE_FREQUENCY = 10f;
  public static final float JOB_UPDATE_FREQUENCY = ROOM_UPDATE_FREQUENCY + 0.1f;
  public static final float PLAYER_EARNOUT_FREQUENCY = 10.f;
  public static final float TRANSPORT_CALCULATOR_FREQUENCY = 0.25f;
  public static final float WEATHER_SERVICE_STATE_CHANGE_FREQUENCY = 60f;
  public static final int WEATHER_SERVICE_STATE_CHANGE_DURATION = 5000;
  public static final int GAME_SAVE_FREQUENCY = 15000;
  public static final String CURRENCY_SYMBOL = "¢";
  public static final long FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK = 3500;
}
