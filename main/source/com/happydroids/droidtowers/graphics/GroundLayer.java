/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.grid.GameGrid;

public class GroundLayer extends GameLayer {
  public GroundLayer(GameGrid gameGrid) {
    super();

    gameGrid.events().register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    removeAllChildren();

    Texture topTexture = new Texture(Gdx.files.internal("backgrounds/ground-top.png"));

    float tiledHeight = TowerConsts.GROUND_HEIGHT - topTexture.getHeight();

    GameObject top = new GameObject(topTexture);
    top.setPosition(-TowerConsts.GAME_WORLD_PADDING, tiledHeight);
    top.setSize(event.gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2), topTexture.getHeight());
    top.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    addChild(top);

    Texture tileTexture = new Texture(Gdx.files.internal("backgrounds/ground-tile.png"));

    GameObject tile = new GameObject(tileTexture);
    tile.setPosition(-TowerConsts.GAME_WORLD_PADDING, 0);
    tile.setSize(event.gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2), tiledHeight);
    tile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    addChild(tile);
  }
}
