package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.unhappyrobot.types.StairType;

public class Stair extends GridObject {
  private Sprite sprite;

  public Stair(StairType stairType, GameGrid gameGrid) {
    super(stairType, gameGrid);

    Texture texture = new Texture(stairType.getImageFilename());
    sprite = new Sprite(texture);
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
