package com.unhappyrobot;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class TowerConsts {
  public static boolean DEBUG = false;
  public static String VERSION = "v0.10.38";
  public static String GIT_SHA = "0950399";
  public static final String HAPPYDROIDS_SERVER = "www.happydroids.com";
  public static final String HAPPYDROIDS_URI = "http://" + HAPPYDROIDS_SERVER;

  public static boolean ENABLE_HAPPYDROIDS_CONNECT = false;

  public static final float ONE_MEGABYTE = 1048576.0f;

  public static final int GRID_UNIT_SIZE = 64;
  public static final int GAME_GRID_START_SIZE = 40;
  public static final int GAME_GRID_EXPAND_LAND_SIZE = 5;
  public static final int GAME_WORLD_PADDING = GRID_UNIT_SIZE * 7;

  public static final int LOBBY_FLOOR = 10;
  public static final float GROUND_HEIGHT = GRID_UNIT_SIZE * LOBBY_FLOOR;

  public static final String CURRENCY_SYMBOL = "$";

  public static final float HUD_UPDATE_FREQUENCY = 1f;
  public static final float ROOM_UPDATE_FREQUENCY = 10f;
  public static final float JOB_UPDATE_FREQUENCY = ROOM_UPDATE_FREQUENCY + 0.1f;
  public static final float PLAYER_EARNOUT_FREQUENCY = 10.f;
  public static final float TRANSPORT_CALCULATOR_FREQUENCY = 1f;
  public static final float WEATHER_SERVICE_STATE_CHANGE_FREQUENCY = 60f;
  public static final int WEATHER_SERVICE_STATE_CHANGE_DURATION = 5000;
  public static final float GAME_SAVE_FREQUENCY = 15f;
  public static final long FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK = 3500;
  public static final float MINI_MAP_REDRAW_FREQUENCY = 1f;
  public static final String GAME_SAVE_DIRECTORY = Gdx.app.getType() == Application.ApplicationType.Android ? "towergame/" : ".towergame/";

  private TowerConsts() {

  }
}
