package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.entities.GuavaSet;
import com.unhappyrobot.grid.GameGrid;

public class CityScapeLayer extends GameLayer {
  private final GameGrid gameGrid;
  private final TextureAtlas cityScapeAtlas;

  public CityScapeLayer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    cityScapeAtlas = new TextureAtlas(Gdx.files.internal("backgrounds/cityscape.txt"));
    layout();
  }

  private void layout() {
    GuavaSet<TextureAtlas.AtlasRegion> regions = new GuavaSet<TextureAtlas.AtlasRegion>(cityScapeAtlas.getRegions());
    while (width() < gameGrid.getWorldSize().x) {
      TextureAtlas.AtlasRegion region = regions.getRandomEntry();

      GameObject sprite = new GameObject(region);
      sprite.setX(width());
      sprite.setY(256f);
      addChild(sprite);
    }
  }

  private float width() {
    float w = 0;

    for (GameObject gameObject : gameObjects) {
      w += gameObject.getWidth();
    }

    return w;
  }
}
