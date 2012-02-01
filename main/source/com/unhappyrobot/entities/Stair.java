package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.types.TransitType;

public class Stair extends Transit {
  private Sprite sprite;

  public Stair(TransitType stairType, GameGrid gameGrid) {
    super(stairType, gameGrid);

    TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal(stairType.getAtlasFilename()));
    sprite = textureAtlas.createSprite("stairs");
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Room || gridObject instanceof Stair;
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    // stairs need to support overflowing into adjacent cells.
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(position.getWorldX(gameGrid), position.getWorldY(gameGrid));
      sprite.draw(spriteBatch);
    }
  }

  @Override
  public Vector2 getContentSize() {
    return size.cpy().add(0, 1);
  }
}
