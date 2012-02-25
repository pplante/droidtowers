package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.math.GridPoint;
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
  public GridPoint getContentSize() {
    GridPoint point = size.cpy();
    point.add(0, 1);
    return point;
  }

  public Vector2 getTopLeftWorldPoint() {
    GridPoint point = new GridPoint(position);
    point.add(0, 1);

    return point.toWorldVector2(gameGrid);
  }

  public Vector2 getBottomRightWorldPoint() {
    GridPoint point = new GridPoint(position);
    point.add(2, 0);

    return point.toWorldVector2(gameGrid);
  }
}
