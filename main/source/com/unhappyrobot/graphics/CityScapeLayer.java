package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.entities.GuavaSet;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GameGridResizeEvent;

public class CityScapeLayer extends GameLayer {
  private final TextureAtlas cityScapeAtlas;

  public CityScapeLayer() {
    cityScapeAtlas = new TextureAtlas(Gdx.files.internal("backgrounds/cityscape.txt"));

    GameEvents.register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    removeAllChildren();

    GuavaSet<TextureAtlas.AtlasRegion> regions = new GuavaSet<TextureAtlas.AtlasRegion>(cityScapeAtlas.getRegions());
    float worldWidth = event.gameGrid.getWorldSize().x;
    while (width() < worldWidth) {
      GameObject sprite = new GameObject(regions.getRandomEntry());
      sprite.setX(width() - gameObjects.size());
      sprite.setY(250f);
      addChild(sprite);
    }
  }
}
