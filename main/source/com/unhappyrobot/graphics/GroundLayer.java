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
  public GroundLayer() {
    super();

    GameGrid.events().register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    removeAllChildren();

    Texture texture = new Texture(Gdx.files.internal("backgrounds/ground.png"));

    GameObject gameObject = new GameObject(texture);
    gameObject.setPosition(-TowerConsts.GAME_WORLD_PADDING, 0);
    gameObject.setSize(event.gameGrid.getWorldSize().x + (TowerConsts.GAME_WORLD_PADDING * 2), 256f);
    gameObject.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    addChild(gameObject);
  }
}
