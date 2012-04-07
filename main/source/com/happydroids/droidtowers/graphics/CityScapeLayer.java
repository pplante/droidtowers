/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.grid.GameGrid;

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
