package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.types.GridObjectType;

public abstract class GridObject {
  protected final GridObjectType gridObjectType;
  protected final GameGrid gameGrid;
  public Vector2 position;
  public Vector2 size;

  public GridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    this.gridObjectType = gridObjectType;
    this.gameGrid = gameGrid;
    this.position = new Vector2(0, 0);
    this.size = new Vector2(1, 1);
  }

  public boolean canShareSpace() {
    return true;
  }

  public Bounds2d getBounds() {
    return new Bounds2d(position, size);
  }

  public GridObjectType getGridObjectType() {
    return gridObjectType;
  }

  public abstract Sprite getSprite();

  public boolean canBeAt() {
    return gridObjectType.canBeAt(this);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  public void render(SpriteBatch spriteBatch) {
    Sprite sprite = getSprite();
    sprite.setPosition(gameGrid.gridOrigin.x + position.x * gameGrid.unitSize.x, gameGrid.gridOrigin.y + position.y * gameGrid.unitSize.y);
    sprite.setSize(size.x * gameGrid.unitSize.x, size.y * gameGrid.unitSize.y);
    sprite.draw(spriteBatch);
  }

  public boolean tap(Vector2 gridPointAtFinger, int count) {
    return false;
  }

  public boolean pan(Vector2 gridPointAtFinger, Vector2 gridPointDelta) {
    return false;
  }

  public boolean touchDown(Vector2 gameGridPoint) {
    return false;
  }
}
