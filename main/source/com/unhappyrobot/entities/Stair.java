package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.types.StairType;

public class Stair extends GridObject {
  private Sprite sprite;

  public Stair(StairType stairType, GameGrid gameGrid) {
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
    // stairs need to support overflowing into ajacent cells.
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(position.getWorldX(gameGrid), position.getWorldY(gameGrid));
      sprite.draw(spriteBatch);
    }
  }
}
