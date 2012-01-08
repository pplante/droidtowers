package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.math.Bounds2d;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.GridObjectType;

public abstract class GridObject {
  protected final GridObjectType gridObjectType;
  protected final GameGrid gameGrid;
  protected GridPoint position;
  protected GridPoint size;
  protected GridObjectState state;
  protected Color renderColor;

  public GridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    this.gridObjectType = gridObjectType;
    this.gameGrid = gameGrid;
    this.position = new GridPoint(gameGrid, 0, 0);
    this.size = new GridPoint(gameGrid, gridObjectType.getWidth(), gridObjectType.getHeight());
    state = GridObjectState.INVALID;
    renderColor = Color.WHITE;
  }

  public boolean canShareSpace(GridObject gridObject) {
    return gridObjectType.canShareSpace(gridObject);
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
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(position.getWorldX(), position.getWorldY());
      sprite.setSize(size.getWorldX(), size.getWorldY());
      sprite.draw(spriteBatch);
    }
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

  public GridPoint getSize() {
    return size;
  }

  public GridPoint getPosition() {
    return position;
  }

  public void setPosition(Vector2 gridPointAtFinger) {
    setPosition(gridPointAtFinger.x, gridPointAtFinger.y);
  }

  public void setPosition(float x, float y) {
    position.set(x, y);
    clampPosition();
    updatePlacementStatus();
  }

  protected void clampPosition() {
    if (position.x < 0) {
      position.x = 0;
    } else if (position.x + size.x > gameGrid.gridSize.x) {
      position.x = gameGrid.gridSize.x - size.x;
    }

    if (position.y < 0) {
      position.y = 0;
    } else if (position.y + size.y > gameGrid.gridSize.y) {
      position.y = gameGrid.gridSize.y - size.y;
    }
  }

  private void updatePlacementStatus() {
    Sprite sprite = getSprite();
    if (sprite != null) {
      if (state.equals(GridObjectState.INVALID)) {
        Color color = new Color(gameGrid.canObjectBeAt(this) ? Color.WHITE : Color.RED);
        color.a = 0.85f;
        renderColor = color;
      } else if (state.equals(GridObjectState.PLACED)) {
        renderColor = Color.WHITE;
      }
    }
  }

  public void update(float deltaTime) {

  }

  public int getCoinsEarned() {
    if (state == GridObjectState.INVALID) {
      return 0;
    }

    return gridObjectType.getCoinsEarned();
  }


  public int getGoldEarned() {
    if (state == GridObjectState.INVALID) {
      return 0;
    }

    return gridObjectType.getGoldEarned();
  }

  public void setState(GridObjectState state) {
    this.state = state;

    updatePlacementStatus();
  }

  public GridObjectState getState() {
    return state;
  }
}
