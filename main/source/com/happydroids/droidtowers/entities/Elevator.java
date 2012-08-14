/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.ElevatorPopOver;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PlacementTool;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.ElevatorType;
import com.happydroids.droidtowers.types.ResizeHandle;

import static com.happydroids.droidtowers.types.ResizeHandle.TOP;

public class Elevator extends Transit {
  public static final int MAX_NUMBER_OF_CARS = 10;
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite emptyShaftSprite;
  private Sprite bottomSprite;
  private ResizeHandle selectedResizeHandle;
  private boolean drawShaft;
  private Action onResizeAction;
  static TextureAtlas elevatorAtlas;
  private GridPoint anchorPoint;
  private int numCars;
  private final Array<ElevatorCar> elevatorCars;
  private final BitmapFontCache floorLabelCache;
  private int numFloorsSinceLabelCacheBuilt;
  private final Vector2 tmpVector;

  public Elevator(ElevatorType elevatorType, final GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    if (elevatorAtlas == null) {
      elevatorAtlas = TowerAssetManager.textureAtlas(elevatorType.getAtlasFilename());
    }

    floorLabelCache = new BitmapFontCache(FontManager.BankGothic32.getFont(), true);

    size.set(1, 3);
    topSprite = elevatorAtlas.createSprite("elevator/top");
    topSprite.setScale(getGridScale());
    bottomSprite = elevatorAtlas.createSprite("elevator/bottom");
    bottomSprite.setScale(getGridScale());
    shaftSprite = TowerAssetManager.sprite("elevator/shaft.png");
    shaftSprite.setScale(getGridScale());
    emptyShaftSprite = TowerAssetManager.sprite("elevator/empty.png");
    emptyShaftSprite.setScale(getGridScale());

    drawShaft = true;

    numCars = 1;
    elevatorCars = new Array<ElevatorCar>(numCars);
    elevatorCars.add(new ElevatorCar(this, elevatorAtlas));
    tmpVector = new Vector2();
  }

  @Override
  public Sprite getSprite() {
    return shaftSprite;
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    for (ElevatorCar elevatorCar : elevatorCars) {
      elevatorCar.update(deltaTime);
    }

    if (numFloorsSinceLabelCacheBuilt != size.y) {
      rebuildFloorLabelCache();
    }
  }

  @Override
  public float getDesirability() {
    return 0;
  }

  @Override
  public boolean shouldUseSpriteCache() {
    return false;
  }

  @Override
  public void render(SpriteBatch spriteBatch, SpriteCache spriteCache, Color renderTintColor) {
    tmpVector.set(worldPosition);

    if (selectedResizeHandle == ResizeHandle.BOTTOM) {
      bottomSprite.setColor(Color.CYAN);
    } else {
      bottomSprite.setColor(renderColor);
    }
    bottomSprite.setPosition(tmpVector.x, tmpVector.y);
    bottomSprite.draw(spriteBatch);

    Sprite shaftToRender;
    if (drawShaft && !(InputSystem.instance().getCurrentTool() instanceof PlacementTool)) {
      shaftToRender = shaftSprite;
    } else {
      shaftToRender = emptyShaftSprite;
    }

    tmpVector.add(0, scaledGridUnit());
    shaftToRender.setColor(renderColor);
    shaftToRender.setPosition(tmpVector.x, tmpVector.y);
    shaftToRender.setSize(worldSize.x, worldSize.y - scaledGridUnit());
    setWrap(shaftToRender);
    shaftToRender.draw(spriteBatch);

    floorLabelCache.setColor(1, 1, 1, 0.5f);
    floorLabelCache.draw(spriteBatch, renderColor.a);

    if (isPlaced() && selectedResizeHandle == null && !(InputSystem.instance()
                                                                .getCurrentTool() instanceof PlacementTool)) {
      for (ElevatorCar elevatorCar : elevatorCars) {
        elevatorCar.setColor(renderColor);
        elevatorCar.draw(spriteBatch);
      }
    }

    if (selectedResizeHandle == TOP) {
      topSprite.setColor(Color.CYAN);
    } else {
      topSprite.setColor(renderColor);
    }
    topSprite.setPosition(getWorldPosition().x, getWorldTop().y - scaledGridUnit());
    topSprite.draw(spriteBatch);
  }

  protected float scaledGridUnit() {
    return TowerConsts.GRID_UNIT_SIZE * getGridScale();
  }

  @Override
  public boolean tap(GridPoint gridPointAtFinger, int count) {
    if (count >= 2) {
      drawShaft = !drawShaft;
      return true;
    }

    return super.tap(gridPointAtFinger, count);
  }

  @Override
  public boolean touchDown(GridPoint gameGridPoint, Vector2 worldPoint, int pointer) {
    if (topSprite.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
      selectedResizeHandle = TOP;
      anchorPoint = position.cpy();

      return true;
    } else if (bottomSprite.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
      selectedResizeHandle = ResizeHandle.BOTTOM;
      anchorPoint = position.cpy().add(size);

      return true;
    } else {
      selectedResizeHandle = null;
    }

    return super.touchDown(gameGridPoint, worldPoint, pointer);
  }

  @Override
  public boolean touchUp() {
    if (selectedResizeHandle != null) {
      selectedResizeHandle = null;
      ElevatorHeightChangeEvent event = Pools.obtain(ElevatorHeightChangeEvent.class);
      event.setGridObject(this);
      broadcastEvent(event);
      Pools.free(event);

      return true;
    }

    return super.touchUp();
  }

  private void rebuildFloorLabelCache() {
    StringBuilder floors = new StringBuilder(size.y * 3);
    for (int localFloorNum = 1; localFloorNum < size.y - 1; localFloorNum++) {
      int worldFloorNum = position.y + localFloorNum;
      int normalizedWorldFloor = worldFloorNum - TowerConsts.LOBBY_FLOOR;
      String labelText = String.valueOf(normalizedWorldFloor);
      if (worldFloorNum == TowerConsts.LOBBY_FLOOR) {
        labelText = "L";
      } else if (worldFloorNum < TowerConsts.LOBBY_FLOOR) {
        labelText = "B" + (TowerConsts.LOBBY_FLOOR - worldFloorNum);
      }
      floors.insert(0, labelText + "\n");
    }

    floorLabelCache.setMultiLineText(floors, getWorldCenterBottom().x, getWorldCenterBottom().y, 0f, BitmapFont.HAlignment.CENTER);
    floorLabelCache.setPosition(0, floorLabelCache.getBounds().height + (TowerConsts.GRID_UNIT_SIZE * 1.25f));

    numFloorsSinceLabelCacheBuilt = size.y;
  }

  @Override
  public boolean pan(GridPoint gridPointAtFinger, GridPoint gridPointDelta) {
    if (selectedResizeHandle == null) {
      return false;
    }

    GridObjectBoundsChangeEvent event = Pools.obtain(GridObjectBoundsChangeEvent.class);
    event.setGridObject(this);

    switch (selectedResizeHandle) {
      case BOTTOM:
        position.y = gridPointAtFinger.y;
        size.y = anchorPoint.y - position.y + 1;
        checkSize();
        position.y = anchorPoint.y - size.y;
        break;

      case TOP:
        size.y = gridPointAtFinger.y - anchorPoint.y;
        checkSize();
        break;
    }

    clampPosition();
    updateWorldCoordinates();

    broadcastEvent(event);
    Pools.free(event);

    return true;
  }

  private void checkSize() {
    size.y = Math.max(size.y, 3);
    size.y = Math.min(size.y, 20);
  }

  public boolean servicesFloor(int floorNumber) {
    float minFloor = getPosition().y + 1;
    float maxFloor = minFloor + getSize().y - 3;

    return minFloor <= floorNumber && floorNumber <= maxFloor;
  }

  public void setWrap(Sprite shaftSprite) {
    Texture texture = shaftSprite.getTexture();

    shaftSprite.setV(0f);
    shaftSprite.setV2(shaftSprite.getHeight() / texture.getHeight());

    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
  }

  @Override
  public void adjustToNewLandSize() {
    for (ElevatorCar elevatorCar : elevatorCars) {
      elevatorCar.clearQueue();
    }

    rebuildFloorLabelCache();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Elevator)) {
      return false;
    }

    Elevator elevator = (Elevator) o;

    return !(elevatorCars != null ? !elevatorCars.equals(elevator.elevatorCars) : elevator.elevatorCars != null);
  }

  @Override
  public int hashCode() {
    return elevatorCars != null ? elevatorCars.hashCode() : 0;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new ElevatorPopOver(this);
  }

  @Override
  protected boolean hasPopOver() {
    return true;
  }

  public boolean addPassenger(AvatarSteeringManager avatarSteeringManager, int currentFloor, int destinationFloor, Runnable uponArrivalRunnable) {
    if (elevatorCars.size == 0) {
      return false;
    }

    for (ElevatorCar elevatorCar : elevatorCars) {
      if (!elevatorCar.isInUse()) {
        return elevatorCar.addPassenger(avatarSteeringManager, currentFloor, destinationFloor, uponArrivalRunnable);
      }
    }

    ElevatorCar elevatorCar = elevatorCars.get(MathUtils.random(0, elevatorCars.size - 1));
    return elevatorCar.addPassenger(avatarSteeringManager, currentFloor, destinationFloor, uponArrivalRunnable);
  }

  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    for (ElevatorCar elevatorCar : elevatorCars) {
      elevatorCar.removePassenger(avatarSteeringManager);
    }
  }

  public int getNumRiders() {
    int totalRiders = 0;
    for (ElevatorCar elevatorCar : elevatorCars) {
      totalRiders += elevatorCar.getNumRiders();
    }

    return totalRiders;
  }

  public int getNumPassengersWaiting() {
    int totalRiders = 0;
    for (ElevatorCar elevatorCar : elevatorCars) {
      totalRiders += elevatorCar.getNumPassengersWaiting();
    }

    return totalRiders;
  }

  public void addCar() {
    numCars = MathUtils.clamp(numCars + 1, 0, MAX_NUMBER_OF_CARS);
    elevatorCars.add(new ElevatorCar(this, elevatorAtlas));
  }

  public int getNumElevatorCars() {
    return elevatorCars.size;
  }

  public void setNumElevatorCars(int numberOfElevatorCars) {
    for (int i = numCars; i < numberOfElevatorCars; i++) {
      addCar();
    }
  }

  @Override
  public int getUpkeepCost() {
    return super.getUpkeepCost() + (gridObjectType.getCoins() / 20 * numCars);
  }

  public void removeCar() {
    numCars = MathUtils.clamp(numCars - 1, 0, MAX_NUMBER_OF_CARS);
    if (elevatorCars.size > 0) {
      ElevatorCar car = elevatorCars.removeIndex(0);
      car.resetToBottomOfShaft();
    }
  }

  public boolean canAddElevatorCar() {
    return elevatorCars.size < MAX_NUMBER_OF_CARS;
  }
}
