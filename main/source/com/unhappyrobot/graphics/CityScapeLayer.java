package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.entities.GuavaSet;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.grid.GameGrid;

public class CityScapeLayer extends GameLayer {
  private final TextureAtlas cityScapeAtlas;

  public CityScapeLayer() {
    cityScapeAtlas = new TextureAtlas(Gdx.files.internal("backgrounds/cityscape.txt"));

    GameGrid.events().register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    GuavaSet<TextureAtlas.AtlasRegion> regions = new GuavaSet<TextureAtlas.AtlasRegion>(cityScapeAtlas.getRegions());
    float worldWidth = event.gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2);
    float nextX = width() - TowerConsts.GAME_WORLD_PADDING - (5f * gameObjects.size());
    while (width() < worldWidth) {
      GameObject sprite = new GameObject(regions.getRandomEntry());
      sprite.setX(nextX);
      sprite.setY(250f);
      addChild(sprite);

      nextX += sprite.getWidth() - 5f;
    }
  }
}
