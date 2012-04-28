/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.actions.TimeDelayedAction;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.events.GridObjectChangedEvent;
import com.happydroids.droidtowers.events.GridObjectEvent;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.GridObjectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GridObject {
  protected final GridObjectType gridObjectType;
  protected final GameGrid gameGrid;
  protected GridPoint position;
  protected GridPoint size;
  protected Vector2 worldPosition;
  protected Vector2 worldSize;
  protected GridObjectPlacementState placementState;
  protected Color renderColor;
  protected Rectangle bounds;
  private Set<Action> actions;
  private EventBus myEventBus;
  protected float desirability;
  private Vector2 worldCenter;
  private Vector2 worldTop;
  private Rectangle worldBounds;

  public GridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    this.gridObjectType = gridObjectType;
    this.gameGrid = gameGrid;

    position = new GridPoint(0, 0);
    size = new GridPoint(gridObjectType.getWidth(), gridObjectType.getHeight());
    bounds = new Rectangle(position.x, position.y, size.x, size.y);

    worldPosition = new Vector2();
    worldSize = new Vector2(size.getWorldX(gameGrid) * gameGrid.getGridScale(), size.getWorldY(gameGrid) * gameGrid.getGridScale());
    worldCenter = new Vector2();
    worldTop = new Vector2();
    worldBounds = new Rectangle();

    placementState = GridObjectPlacementState.INVALID;
    actions = new HashSet<Action>();
    setRenderColor(Color.WHITE);
  }

  public boolean canShareSpace(GridObject gridObject) {
    return gridObjectType.canShareSpace(gridObject);
  }

  public Rectangle getBounds() {
    return bounds;
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

  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    Sprite sprite = getSprite();
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(worldPosition.x, worldPosition.y);
      sprite.setSize(worldSize.x, worldSize.y);
      sprite.draw(spriteBatch);
    }
  }

  public boolean tap(Vector2 gridPointAtFinger, int count) {
    return false;
  }

  public boolean pan(Vector2 gridPointAtFinger, Vector2 gridPointDelta) {
    return false;
  }

  public boolean touchDown(Vector2 gameGridPoint, Vector2 worldPoint, int pointer) {
    return false;
  }

  public boolean touchUp() {
    return false;
  }

  public GridPoint getSize() {
    return size;
  }

  public void setSize(GridPoint size) {
    setSize(size.x, size.y);
  }

  public void setSize(float x, float y) {
    size.set(x, y);
    updateWorldCoordinates();
  }

  public GridPoint getPosition() {
    return position;
  }

  public void setPosition(Vector2 gridPointAtFinger) {
    setPosition(gridPointAtFinger.x, gridPointAtFinger.y);
  }

  public void setPosition(float x, float y) {
    GridPoint prevPosition = position.cpy();

    position.set(x, y);
    clampPosition();
    updateWorldCoordinates();
    updatePlacementStatus();

    broadcastEvent(new GridObjectBoundsChangeEvent(this, size, prevPosition));
  }

  protected void updateWorldCoordinates() {
    worldPosition.set(gameGrid.getGridOrigin().x + (position.getWorldX(gameGrid) * gameGrid.getGridScale()), gameGrid.getGridOrigin().y + (position.getWorldY(gameGrid) * gameGrid.getGridScale()));
    worldSize.set(size.getWorldX(gameGrid) * gameGrid.getGridScale(), size.getWorldY(gameGrid) * gameGrid.getGridScale());
    worldBounds.set(worldPosition.x, worldPosition.y, worldSize.x, worldSize.y);
    worldCenter.set(worldPosition.x + worldSize.x / 2, worldPosition.y + worldSize.y / 2);
    worldTop.set(worldPosition.x + worldSize.x / 2, worldPosition.y + worldSize.y);
  }

  protected void clampPosition() {
    if (position.x < 0) {
      position.x = 0;
    } else if (position.x + size.x > gameGrid.getGridSize().x) {
      position.x = gameGrid.getGridSize().x - size.x;
    }

    if (position.y < 0) {
      position.y = 0;
    } else if (position.y + size.y > gameGrid.getGridSize().y) {
      position.y = gameGrid.getGridSize().y - size.y;
    }
  }

  private void updatePlacementStatus() {
    Sprite sprite = getSprite();
    if (sprite != null) {
      if (placementState.equals(GridObjectPlacementState.INVALID)) {
        setRenderColor(gameGrid.canObjectBeAt(this) ? Color.CYAN : Color.RED);
      } else if (placementState.equals(GridObjectPlacementState.PLACED)) {
        setRenderColor(Color.WHITE);
        broadcastEvent(new GridObjectPlacedEvent(this));
      }
    }

    broadcastEvent(new GridObjectChangedEvent(this, "placementStatus"));
  }

  public void update(float deltaTime) {
    if (placementState.equals(GridObjectPlacementState.PLACED)) {
      long currentTime = System.currentTimeMillis();

      for (Action action : actions) {
        action.act(currentTime);
      }
    }
  }


  public int getCoinsEarned() {
    if (placementState == GridObjectPlacementState.INVALID) {
      return 0;
    }

    return gridObjectType.getCoinsEarned();
  }

  public int getUpkeepCost() {
    if (placementState == GridObjectPlacementState.INVALID) {
      return 0;
    }

    return gridObjectType.getUpkeepCost();
  }

  public void setPlacementState(GridObjectPlacementState placementState) {
    this.placementState = placementState;
    updatePlacementStatus();
  }

  public GridObjectPlacementState getPlacementState() {
    return placementState;
  }


  protected void addAction(TimeDelayedAction action) {
    actions.add(action);
  }

  public float getNoiseLevel() {
    return gridObjectType.getNoiseLevel();
  }

  public GridPoint getContentSize() {
    return size;
  }

  public GridPoint getContentPosition() {
    return position;
  }

  public List<GridPoint> getGridPointsOccupied() {
    List<GridPoint> points = Lists.newArrayList();

    for (float x = position.x; x < position.x + size.x; x += 1f) {
      for (float y = position.y; y < position.y + size.y; y += 1f) {
        points.add(new GridPoint(x, y));
      }
    }

    return points;
  }


  public List<GridPoint> getGridPointsTouched() {
    return getGridPointsOccupied();
  }

  public float distanceToLobby() {
    return position.y - TowerConsts.LOBBY_FLOOR;
  }

  public float distanceFromFloor(float originalFloor) {
    return originalFloor - position.y;
  }

  public Vector2 getWorldCenter() {
    return worldCenter;
  }

  public Vector2 getWorldTop() {
    return worldTop;
  }

  public void setRenderColor(Color renderColor) {
    Color baseTint = new Color(gameGrid.getRenderer().getRenderTintColor());
    this.renderColor = baseTint.mul(renderColor);
  }

  @Override
  public String toString() {
    return "GridObject{" +
                   "position=" + position +
                   ", gridObjectType=" + gridObjectType +
                   '}';
  }

  public EventBus eventBus() {
    if (myEventBus == null) {
      myEventBus = new EventBus(this.getClass().getSimpleName());
    }

    return myEventBus;
  }

  protected void broadcastEvent(GridObjectEvent event) {
    if (myEventBus != null) {
      myEventBus.post(event);
    }

    gameGrid.events().post(event);
  }

  public float getDesirability() {
    return desirability;
  }

  protected float getGridScale() {
    return gameGrid.getGridScale();
  }

  public Vector2 getWorldPosition() {
    return worldPosition;
  }

  public Rectangle getWorldBounds() {
    return worldBounds;
  }
}
