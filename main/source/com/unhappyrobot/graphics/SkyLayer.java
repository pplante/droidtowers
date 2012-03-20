package com.unhappyrobot.graphics;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.WeatherService;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.WeatherStateChangeEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.tween.GameObjectAccessor;
import com.unhappyrobot.tween.TweenSystem;

public class SkyLayer extends GameLayer {
  private final GameGrid gameGrid;
  private final WeatherService weatherService;
  private final GameObject sky;

  public SkyLayer(GameGrid gameGrid, WeatherService weatherService) {
    super();
    this.gameGrid = gameGrid;
    this.weatherService = weatherService;

    gameGrid.events().register(this);
    weatherService.events().register(this);

    Texture texture = new Texture(Gdx.files.internal("backgrounds/sky-gradient.png"));
    sky = new GameObject(texture);
    GameGrid_onResize(null);
    sky.setColor(weatherService.currentState().color);

    addChild(sky);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    sky.setPosition(-TowerConsts.GAME_WORLD_PADDING, 256);
    sky.setSize(gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2), gameGrid.getWorldSize().y + TowerConsts.GAME_WORLD_PADDING);
    sky.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
  }

  @Subscribe
  public void WeatherService_onWeatherChange(WeatherStateChangeEvent event) {
    Color tweenColor = weatherService.currentState().color;

    Tween.to(sky, GameObjectAccessor.COLOR, TowerConsts.WEATHER_SERVICE_STATE_CHANGE_DURATION)
            .target(tweenColor.r, tweenColor.g, tweenColor.b, tweenColor.a)
            .start(TweenSystem.getTweenManager());
  }
}
