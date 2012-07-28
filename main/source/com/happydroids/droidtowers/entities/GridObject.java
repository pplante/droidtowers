/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.events.*;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.List;
import java.util.Set;

public abstract class GridObject {
  protected final GridObjectType gridObjectType;
  protected final GameGrid gameGrid;
  protected GridPoint position;
  protected GridPoint size;
  protected Vector2 worldPosition;
  protected Vector2 worldSize;
  protected Color renderColor;
  protected Rectangle bounds;
  private Set<Action> actions;
  private EventBus myEventBus;
  private Vector2 worldCenter;
  private Vector2 worldTop;
  private Rectangle worldBounds;
  protected boolean placed;
  protected boolean connectedToTransport;
  private boolean connectedToSecurity;
  protected int numVisitors;
  protected long lastCleanedAt;
  protected float surroundingNoiseLevel;
  protected float surroundingCrimeLevel;
  protected String name;
  private boolean displayedPopOver;
  protected int loanFromCousinVinnie;
  private int variationId;
  private List<GridPoint> pointsTouched;
  private Set<Avatar> visitorQueue;


  public GridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    this.gridObjectType = gridObjectType;
    this.gameGrid = gameGrid;

    name = NameGenerator.randomNameForGridObjectType(getGridObjectType());
    position = new GridPoint(0, 0);
    size = new GridPoint(gridObjectType.getWidth(), gridObjectType.getHeight());
    bounds = new Rectangle(position.x, position.y, size.x, size.y);
    visitorQueue = Sets.newHashSet();

    worldPosition = new Vector2();
    worldSize = new Vector2(size.getWorldX() * gameGrid.getGridScale(), size.getWorldY() * gameGrid.getGridScale());
    worldCenter = new Vector2();
    worldTop = new Vector2();
    worldBounds = new Rectangle();

    if (gridObjectType.getNumVariations() > 0) {
      variationId = MathUtils.random(1, gridObjectType.getNumVariations());
    }

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

  public void updateSprite() {

  }

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

  public boolean tap(GridPoint gridPointAtFinger, int count) {
    return false;
  }

  public boolean pan(GridPoint gridPointAtFinger, GridPoint gridPointDelta) {
    return false;
  }

  public boolean touchDown(GridPoint gameGridPoint, Vector2 worldPoint, int pointer) {
    if (displayedPopOver) {
      displayedPopOver = false;
      return false;
    }

    return hasPopOver();
  }

  public boolean touchUp() {
    GridObjectPopOver popOver = makePopOver();
    if (popOver != null) {
      displayedPopOver = true;
      popOver.pack();
      popOver.color.a = 0f;
      popOver.action(FadeIn.$(0.125f));
      HeadsUpDisplay.instance().addActor(popOver);
      return true;
    }

    return false;
  }

  public GridPoint getSize() {
    return size;
  }

  public void setSize(GridPoint size) {
    setSize(size.x, size.y);
  }

  public void setSize(int x, int y) {
    size.set(x, y);
    updateWorldCoordinates();
    broadcastEvent(new GridObjectBoundsChangeEvent(this, size, position));
  }

  public GridPoint getPosition() {
    return position;
  }

  public void setPosition(GridPoint gridPointAtFinger) {
    setPosition(gridPointAtFinger.x, gridPointAtFinger.y);
  }

  public void setPosition(int x, int y) {
    GridPoint prevPosition = position.cpy();

    position.set(x, y);
    clampPosition();
    updateWorldCoordinates();

    if (!position.equals(prevPosition)) {
      checkPlacement(placed);
      broadcastEvent(new GridObjectBoundsChangeEvent(this, size, prevPosition));
    }
  }

  public void updateWorldCoordinates() {
    worldPosition.set(gameGrid.getGridOrigin().x + (position.getWorldX() * gameGrid.getGridScale()), gameGrid.getGridOrigin().y + (position.getWorldY() * gameGrid.getGridScale()));
    worldSize.set(size.getWorldX() * gameGrid.getGridScale(), size.getWorldY() * gameGrid.getGridScale());
    worldBounds.set(worldPosition.x, worldPosition.y, worldSize.x, worldSize.y);
    worldCenter.set(worldPosition.x + worldSize.x / 2, worldPosition.y + worldSize.y / 2);
    worldTop.set(worldPosition.x + worldSize.x / 2, worldPosition.y + worldSize.y);

    updateGridPointsTouched();
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

  public int getCoinsEarned() {
    return placed ? gridObjectType.getCoinsEarned() : 0;
  }

  public int getUpkeepCost() {
    return 0;
  }

  public void setPlaced(boolean state) {
    boolean prevState = placed;
    placed = state;

    checkPlacement(prevState);
  }

  private void checkPlacement(boolean prevState) {
    if (placed) {
      setRenderColor(Color.WHITE);
      broadcastEvent(new GridObjectPlacedEvent(this));
    } else {
      setRenderColor(gameGrid.canObjectBeAt(this) ? Color.CYAN : Color.RED);
    }

    if (placed != prevState) {
      broadcastEvent(new GridObjectChangedEvent(this, "placementStatus"));
    }
  }

  public boolean isPlaced() {
    return placed;
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

  public List<GridPoint> getGridPointsTouched() {
    return pointsTouched;
  }

  public void updateGridPointsTouched() {
    pointsTouched = Lists.newArrayList();

    GridPoint contentSize = getContentSize();
    GridPoint contentPosition = getContentPosition();
    for (int x = contentPosition.x; x < contentPosition.x + contentSize.x; x++) {
      for (int y = contentPosition.y; y < contentPosition.y + contentSize.y; y++) {
        pointsTouched.add(new GridPoint(x, y));
      }
    }
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

  public EventBus eventBus() {
    if (myEventBus == null) {
      myEventBus = new SafeEventBus();
    }

    return myEventBus;
  }

  public void update(float deltaTime) {
  }

  public void broadcastEvent(GridObjectEvent event) {
    if (myEventBus != null) {
      myEventBus.post(event);
    }

    gameGrid.events().post(event);
  }

  public abstract float getDesirability();

  protected float getGridScale() {
    return gameGrid.getGridScale();
  }

  public Vector2 getWorldPosition() {
    return worldPosition;
  }

  public Rectangle getWorldBounds() {
    return worldBounds;
  }

  public void adjustToNewLandSize() {

  }

  public Vector2 getWorldCenterBottom() {
    return worldCenter.cpy().sub(0, TowerConsts.GRID_UNIT_SIZE * size.y / 2);
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GridObject)) return false;

    GridObject that = (GridObject) o;

    if (gameGrid != null ? !gameGrid.equals(that.gameGrid) : that.gameGrid != null) return false;
    if (gridObjectType != null ? !gridObjectType.equals(that.gridObjectType) : that.gridObjectType != null)
      return false;
    if (placed != that.placed) return false;
    if (position != null ? !position.equals(that.position) : that.position != null) return false;
    if (size != null ? !size.equals(that.size) : that.size != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = gridObjectType != null ? gridObjectType.hashCode() : 0;
    result = 31 * result + (gameGrid != null ? gameGrid.hashCode() : 0);
    result = 31 * result + (position != null ? position.hashCode() : 0);
    result = 31 * result + (size != null ? size.hashCode() : 0);
    result = 31 * result + (placed ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GridObject{" +
                   "position=" + position +
                   ", gridObjectType=" + gridObjectType +
                   '}';
  }

  public float getCrimeLevel() {
    return loanFromCousinVinnie > 0 ? 1f : gridObjectType.getCrimeLevel();
  }

  public boolean isConnectedToSecurity() {
    return connectedToSecurity;
  }

  public void setConnectedToTransport(boolean connectedToTransport) {
    this.connectedToTransport = connectedToTransport;
  }

  public boolean isConnectedToTransport() {
    return connectedToTransport && placed;
  }

  public void setConnectedToSecurity(boolean connectedToSecurity) {
    this.connectedToSecurity = connectedToSecurity;
  }

  public boolean provides(ProviderType... providerType) {
    return gridObjectType.provides(providerType);
  }

  public int getNumVisitors() {
    return numVisitors;
  }

  public long getLastServicedAt() {
    return lastCleanedAt;
  }

  public void recordVisitor(Avatar avatar) {
    visitorQueue.remove(avatar);

    if (Janitor.class.isInstance(avatar)) {
      numVisitors = 0;
      lastCleanedAt = System.currentTimeMillis();
    } else {
      numVisitors += 1;
    }
  }

  public float getNormalizedCrimeLevel() {
    if (getCrimeLevel() > 0f) {
      return getCrimeLevel() * Math.max(1, getNumVisitors()) - gameGrid.positionCache().getPosition(position).normalizedDistanceFromSecurity;
    } else {
      return 0;
    }
  }

  public void setSurroundingNoiseLevel(float noise) {
    surroundingNoiseLevel = noise;
  }

  public void setSurroundingCrimeLevel(float crimeLevel) {
    surroundingCrimeLevel = crimeLevel;
  }

  public float getSurroundingNoiseLevel() {
    return surroundingNoiseLevel;
  }

  public float getSurroundingCrimeLevel() {
    return surroundingCrimeLevel;
  }

  public abstract GridObjectPopOver makePopOver();

  public String getName() {
    return name != null ? name : gridObjectType.getName();
  }

  public boolean hasCustomName() {
    return name != null;
  }

  public void setName(String name) {
    this.name = name;
  }

  protected abstract boolean hasPopOver();

  public void addLoanFromCousinVinnie(int amountLoaned) {
    loanFromCousinVinnie += amountLoaned;
  }

  public int getAmountLoanedFromCousinVinnie() {
    return loanFromCousinVinnie;
  }

  public boolean hasLoanFromCousinVinnie() {
    return loanFromCousinVinnie > 0;
  }

  public int getVariationId() {
    return variationId;
  }

  public void setVariationId(int variationId) {
    this.variationId = variationId;
  }


  public Set<Avatar> getVisitorQueue() {
    return visitorQueue;
  }

  public void removeLoanFromVinnie() {
    loanFromCousinVinnie = 0;
  }

  public boolean canEarnMoney() {
    return true;
  }
}
