/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.graphics;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerAssetManager;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.grid.GameGrid;

import java.util.Iterator;

public class CityScapeLayer extends GameLayer {
  private final TextureAtlas cityScapeAtlas;
  private final Iterator<TextureAtlas.AtlasRegion> regions;

  public CityScapeLayer(GameGrid gameGrid) {
    cityScapeAtlas = TowerAssetManager.textureAtlas("backgrounds/cityscape.txt");
    regions = Iterables.cycle(cityScapeAtlas.getRegions()).iterator();

    gameGrid.events().register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    float worldWidth = event.gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2);
    float nextX = width() - TowerConsts.GAME_WORLD_PADDING - (5f * gameObjects.size());
    while (width() < worldWidth) {
      GameObject sprite = new GameObject(regions.next());
      sprite.setX(nextX);
      sprite.setY(TowerConsts.GROUND_HEIGHT - 5f);
      addChild(sprite);

      nextX += sprite.getWidth() - 5f;
    }
  }
}
