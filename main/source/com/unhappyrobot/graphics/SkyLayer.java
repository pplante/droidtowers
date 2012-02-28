package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GameGridResizeEvent;

public class SkyLayer extends GameLayer {
  public SkyLayer() {
    super();

    GameEvents.register(this);
  }

  @Subscribe
  public void GameGrid_onResize(GameGridResizeEvent event) {
    removeAllChildren();

    Texture texture = new Texture(Gdx.files.internal("backgrounds/stormysky.png"));

    GameObject gameObject = new GameObject(texture);
    gameObject.setPosition(0, 256);
    gameObject.setSize(event.gameGrid.getWorldSize().x, event.gameGrid.getWorldSize().y - 256f);
    gameObject.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    addChild(gameObject);
  }
}
