/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.ElevatorType;
import com.happydroids.droidtowers.types.ResizeHandle;

import java.util.HashMap;
import java.util.List;

import static com.happydroids.droidtowers.types.ResizeHandle.TOP;

public class Elevator extends Transit {
  public static final int MAX_NUMBER_OF_CARS = 10;
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite emptyShaftSprite;
  private Sprite bottomSprite;
  private final BitmapFont floorFont;
  private ResizeHandle selectedResizeHandle;
  private boolean drawShaft;
  private Action onResizeAction;
  static TextureAtlas elevatorAtlas;
  private GridPoint anchorPoint;
  private final HashMap<Integer, String> shaftLabels;
  private int numCars;
  private final List<ElevatorCar> elevatorCars;


  public Elevator(ElevatorType elevatorType, final GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    if (elevatorAtlas == null) {
      elevatorAtlas = TowerAssetManager.textureAtlas(elevatorType.getAtlasFilename());
    }

    size.set(1, 3);
    topSprite = elevatorAtlas.createSprite("elevator/top");
    topSprite.setScale(getGridScale());
    bottomSprite = elevatorAtlas.createSprite("elevator/bottom");
    bottomSprite.setScale(getGridScale());
    shaftSprite = TowerAssetManager.sprite("elevator/shaft.png");
    shaftSprite.setScale(getGridScale());
    emptyShaftSprite = TowerAssetManager.sprite("elevator/empty.png");
    emptyShaftSprite.setScale(getGridScale());
    floorFont = FontManager.BankGothic32.getFont();

    drawShaft = true;

    elevatorCars = Lists.newArrayList();
    numCars = 1;
    for (int i = 0; i < numCars; i++) {
      elevatorCars.add(new ElevatorCar(this, elevatorAtlas));
    }

    shaftLabels = Maps.newHashMap();
  }

  @Override
  public Sprite getSprite() {
    return shaftSprite;
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    for (int i = 0, elevatorCarsSize = elevatorCars.size(); i < elevatorCarsSize; i++) {
      ElevatorCar elevatorCar = elevatorCars.get(i);
      elevatorCar.update(deltaTime);
    }
  }

  @Override
  public float getDesirability() {
    return 0;
  }

  @Override
  public void render(SpriteBatch spriteBatch, Color renderTintColor) {
    GridPoint renderPosition = position.cpy();
    Vector2 localPoint = worldPosition.cpy();

    if (selectedResizeHandle == ResizeHandle.BOTTOM) {
      bottomSprite.setColor(Color.CYAN);
    } else {
      bottomSprite.setColor(renderColor);
    }
    bottomSprite.setPosition(localPoint.x, localPoint.y);
    bottomSprite.draw(spriteBatch);

    Sprite shaftToRender = drawShaft ? shaftSprite : emptyShaftSprite;
    BitmapFont.TextBounds textBounds;


    localPoint.add(0, scaledGridUnit());
    shaftToRender.setColor(renderColor);
    shaftToRender.setPosition(localPoint.x, localPoint.y);
    shaftToRender.setSize(worldSize.x, worldSize.y - scaledGridUnit());
    setWrap(shaftToRender);
    shaftToRender.draw(spriteBatch);

    for (int localFloorNum = 1; localFloorNum < size.y - 1; localFloorNum++) {
      int worldFloorNum = position.y + localFloorNum;
      int normalizedWorldFloor = worldFloorNum - TowerConsts.LOBBY_FLOOR;
      if (!shaftLabels.containsKey(normalizedWorldFloor)) {
        String labelText = String.valueOf(normalizedWorldFloor);
        if (worldFloorNum == TowerConsts.LOBBY_FLOOR) {
          labelText = "L";
        } else if (worldFloorNum < TowerConsts.LOBBY_FLOOR) {
          labelText = "B" + (TowerConsts.LOBBY_FLOOR - worldFloorNum);
        }
        shaftLabels.put(normalizedWorldFloor, labelText);
      }

      textBounds = floorFont.getBounds(shaftLabels.get(normalizedWorldFloor));
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.setScale(getGridScale());
      floorFont.draw(spriteBatch, shaftLabels.get(normalizedWorldFloor), worldPosition.x + ((TowerConsts.GRID_UNIT_SIZE - textBounds.width) / 2), worldPosition.y + (scaledGridUnit() * localFloorNum) + ((TowerConsts.GRID_UNIT_SIZE - textBounds.height) / 2));
    }

    for (ElevatorCar elevatorCar : elevatorCars) {
      elevatorCar.setColor(renderColor);
      elevatorCar.draw(spriteBatch);
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
    }

    return true;
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
      broadcastEvent(new ElevatorHeightChangeEvent(this));

      return true;
    }

    return super.touchUp();
  }

  @Override
  public boolean pan(GridPoint gridPointAtFinger, GridPoint gridPointDelta) {
    if (selectedResizeHandle == null) {
      return false;
    }

    GridPoint newSize = size.cpy();
    GridPoint prevSize = size.cpy();
    GridPoint newPosition = position.cpy();
    GridPoint oldPosition = position.cpy();

    switch (selectedResizeHandle) {
      case BOTTOM:
        newPosition.y = gridPointAtFinger.y;
        newSize.y = anchorPoint.y - newPosition.y + 1;
        checkSize(newSize);
        newPosition.y = anchorPoint.y - newSize.y;
        break;

      case TOP:
        newSize.y = gridPointAtFinger.y - anchorPoint.y;
        checkSize(newSize);
        break;
    }

    size.set(newSize);
    setPosition(newPosition);
    broadcastEvent(new GridObjectBoundsChangeEvent(this, prevSize, oldPosition));

    return true;
  }

  private void checkSize(GridPoint newSize) {
    newSize.y = Math.max(newSize.y, 3);
    newSize.y = Math.min(newSize.y, 20);
  }

  @Override
  public GridPoint getContentSize() {
    GridPoint cpy = size.cpy();
    cpy.sub(0, 2);
    return cpy;
  }

  @Override
  public GridPoint getContentPosition() {
    GridPoint cpy = position.cpy();
    cpy.add(0, 1);
    return cpy;
  }

  public boolean servicesFloor(int floorNumber) {
    float minFloor = getContentPosition().y;
    float maxFloor = minFloor + getContentSize().y;
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
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Elevator)) return false;

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
    if (elevatorCars.isEmpty()) {
      return false;
    }

    for (ElevatorCar elevatorCar : elevatorCars) {
      if (!elevatorCar.isInUse()) {
        return elevatorCar.addPassenger(avatarSteeringManager, currentFloor, destinationFloor, uponArrivalRunnable);
      }
    }

    ElevatorCar elevatorCar = elevatorCars.get(MathUtils.random(0, elevatorCars.size() - 1));
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
    return elevatorCars.size();
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
    if (!elevatorCars.isEmpty()) {
      ElevatorCar car = elevatorCars.remove(0);
      car.resetToBottomOfShaft();
    }
  }

  public boolean canAddElevatorCar() {
    return elevatorCars.size() < MAX_NUMBER_OF_CARS;
  }
}
