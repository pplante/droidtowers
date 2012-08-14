/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pools;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.events.GridObjectEvent;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.math.StatLog;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class GridObject {
  public static final float VISITORS_PER_CLEANING = 35f;
  protected static Map<String, TextureRegion> availableDecals;
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
  private Vector2 worldTop;
  private Vector2 worldCenter;
  private Vector2 worldCenterBottom;
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
  private Avatar beingServicedBy;
  private StatLog averageNumVisitors;
  private int spriteCacheId = -1;
  protected Set<String> decalsToDraw;


  public GridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    this.gridObjectType = gridObjectType;
    this.gameGrid = gameGrid;

    name = NameGenerator.randomNameForGridObjectType(getGridObjectType());
    position = new GridPoint(0, 0);
    size = new GridPoint(gridObjectType.getWidth(), gridObjectType.getHeight());
    bounds = new Rectangle(position.x, position.y, size.x, size.y);
    visitorQueue = Sets.newHashSet();
    averageNumVisitors = new StatLog();
    averageNumVisitors.reset(5);

    worldPosition = new Vector2();
    worldSize = new Vector2(size.getWorldX() * gameGrid.getGridScale(), size.getWorldY() * gameGrid.getGridScale());
    worldCenter = new Vector2();
    worldCenterBottom = new Vector2();
    worldTop = new Vector2();
    worldBounds = new Rectangle();

    if (gridObjectType.getNumVariations() > 0) {
      variationId = MathUtils.random(1, gridObjectType.getNumVariations());
    }

    setRenderColor(Color.WHITE);
    decalsToDraw = Sets.newHashSet();
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

  public void render(SpriteBatch spriteBatch, SpriteCache spriteCache, Color renderTintColor) {
    Sprite sprite = getSprite();
    if (sprite != null) {
      sprite.setColor(renderColor);
      sprite.setPosition(worldPosition.x, worldPosition.y);
      sprite.setSize(worldSize.x, worldSize.y);

      if (shouldUseSpriteCache()) {
        spriteCache.beginCache();
        spriteCache.add(sprite);
        setSpriteCacheId(spriteCache.endCache());
      } else {
        sprite.draw(spriteBatch);
      }
    }
  }

  public void renderDecals(SpriteBatch spriteBatch) {
    if (hasDecals()) {
      spriteBatch.setColor(Color.WHITE);

      if (decalsToDraw.size() == 1) {
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          spriteBatch.draw(region, getWorldCenter().x - region.getRegionWidth() / 2, getWorldCenter().y - region.getRegionHeight() / 2);
        }
      } else {
        int decalsWidth = 0;
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          decalsWidth = region.getRegionWidth();
        }

        float startX = getWorldCenter().x - decalsWidth;
        for (String regionName : decalsToDraw) {
          TextureRegion region = availableDecals.get(regionName);
          spriteBatch.draw(region, startX, getWorldCenter().y - region.getRegionHeight() / 2);
          startX += region.getRegionWidth();
        }
      }
    }
  }

  public boolean hasDecals() {
    return !decalsToDraw.isEmpty();
  }

  public boolean tap(GridPoint gridPointAtFinger, int count) {
    if (count == 1 && hasPopOver()) {
      if (displayedPopOver) {
        displayedPopOver = false;
      } else {
        GridObjectPopOver popOver = makePopOver();
        if (popOver != null) {
          SceneManager.activeScene().getCameraController().panTo(getWorldCenter().x, getWorldCenter().y, true);
          displayedPopOver = true;
          popOver.pack();
          popOver.getColor().a = 0f;
          popOver.addAction(Actions.fadeIn(0.125f));
          HeadsUpDisplay.instance().addActor(popOver);
          return true;
        }
      }
    }

    return false;
  }

  public boolean pan(GridPoint gridPointAtFinger, GridPoint gridPointDelta) {
    return false;
  }

  public GridPoint getSize() {
    return size;
  }

  public void setSize(GridPoint size) {
    setSize(size.x, size.y);
  }

  public void setSize(int x, int y) {
    GridObjectBoundsChangeEvent event = Pools.obtain(GridObjectBoundsChangeEvent.class);
    event.setGridObject(this);

    size.set(x, y);
    updateWorldCoordinates();

    broadcastEvent(event);
    Pools.free(event);
  }

  public GridPoint getPosition() {
    return position;
  }

  public void setPosition(GridPoint gridPointAtFinger) {
    setPosition(gridPointAtFinger.x, gridPointAtFinger.y);
  }

  public void setPosition(int x, int y) {
    GridObjectBoundsChangeEvent event = Pools.obtain(GridObjectBoundsChangeEvent.class);
    event.setGridObject(this);

    position.set(x, y);
    clampPosition();
    updateWorldCoordinates();

    checkPlacement(placed);
    broadcastEvent(event);
    Pools.free(event);
  }

  public void updateWorldCoordinates() {
    worldPosition.set(gameGrid.getGridOrigin().x + (position.getWorldX() * gameGrid.getGridScale()), gameGrid.getGridOrigin().y + (position.getWorldY() * gameGrid.getGridScale()));
    worldSize.set(size.getWorldX() * gameGrid.getGridScale(), size.getWorldY() * gameGrid.getGridScale());
    worldBounds.set(worldPosition.x, worldPosition.y, worldSize.x, worldSize.y);
    worldCenter.set(worldPosition.x + worldSize.x / 2, worldPosition.y + worldSize.y / 2);
    worldCenterBottom.set(worldCenter.cpy().sub(0, TowerConsts.GRID_UNIT_SIZE * size.y / 2));
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
      GridObjectPlacedEvent event = Pools.obtain(GridObjectPlacedEvent.class);
      event.setGridObject(this);
      broadcastEvent(event);
      Pools.free(event);
    } else {
      setRenderColor(gameGrid.canObjectBeAt(this) ? Color.CYAN : Color.RED);
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

    for (int x = position.x; x < position.x + size.x; x++) {
      for (int y = position.y; y < position.y + size.y; y++) {
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
    return worldCenterBottom;
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GridObject)) {
      return false;
    }

    GridObject that = (GridObject) o;

    if (gameGrid != null ? !gameGrid.equals(that.gameGrid) : that.gameGrid != null) {
      return false;
    }
    if (gridObjectType != null ? !gridObjectType.equals(that.gridObjectType) : that.gridObjectType != null) {
      return false;
    }
    if (placed != that.placed) {
      return false;
    }
    if (position != null ? !position.equals(that.position) : that.position != null) {
      return false;
    }
    if (size != null ? !size.equals(that.size) : that.size != null) {
      return false;
    }

    return true;
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
    removeFromVisitorQueue(avatar);

    numVisitors += 1;

    if (avatar instanceof Janitor || avatar instanceof Maid) {
      Janitor janitor = (Janitor) avatar;
      if (provides(janitor.servicesTheseProviderTypes)) {
        lastCleanedAt = System.currentTimeMillis();
        averageNumVisitors.record(numVisitors);
        numVisitors = 0;
      }
    }
  }

  public float getNormalizedCrimeLevel() {
    if (getCrimeLevel() > 0f) {
      return getCrimeLevel() * Math.max(1, getNumVisitors()) - gameGrid.positionCache()
                                                                       .getPosition(position).normalizedDistanceFromSecurity;
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

  public void removeLoanFromVinnie() {
    loanFromCousinVinnie = 0;
  }

  public boolean canEarnMoney() {
    return true;
  }

  public void addToVisitorQueue(Avatar avatar) {
    visitorQueue.add(avatar);

    if (avatar instanceof Janitor && !(avatar instanceof SecurityGuard)) {
      beingServicedBy = avatar;
    }
  }

  public void removeFromVisitorQueue(Avatar avatar) {
    visitorQueue.remove(avatar);

    if (beingServicedBy == avatar) {
      beingServicedBy = null;
    }
  }

  public int getVisitorQueueSize() {
    return visitorQueue.size();
  }

  public boolean isBeingServiced() {
    return beingServicedBy != null;
  }

  protected float getAvgNumVisitors() {
    return averageNumVisitors.getAverage();
  }

  public float getDirtLevel() {
    return MathUtils.clamp(getNumVisitors(), 0, VISITORS_PER_CLEANING) / VISITORS_PER_CLEANING;
  }

  public boolean touchDown(GridPoint gameGridPoint, Vector2 worldPoint, int pointer) {
    return false;
  }

  public boolean touchUp() {
    return false;
  }

  public boolean shouldUseSpriteCache() {
    return isPlaced();
  }

  public int getSpriteCacheId() {
    return spriteCacheId;
  }

  public void setSpriteCacheId(int spriteCacheId) {
    this.spriteCacheId = spriteCacheId;
  }

  public boolean needsDroids() {
    return false;
  }
}
