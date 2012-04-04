/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.grid.GameGrid;

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
