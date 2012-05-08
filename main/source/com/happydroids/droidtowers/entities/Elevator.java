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
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.ElevatorType;
import com.happydroids.droidtowers.types.ResizeHandle;

import static com.happydroids.droidtowers.types.ResizeHandle.TOP;

public class Elevator extends Transit {
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite emptyShaftSprite;
  private Sprite bottomSprite;
  private final BitmapFont floorFont;
  private ResizeHandle selectedResizeHandle;
  private boolean drawShaft;
  private Action onResizeAction;
  private ElevatorCar elevatorCar;
  static TextureAtlas elevatorAtlas;
  private GridPoint anchorPoint;

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

    elevatorCar = new ElevatorCar(this, elevatorAtlas);
  }

  @Override
  public Sprite getSprite() {
    return shaftSprite;
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    elevatorCar.update(deltaTime);
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
      String labelText = String.valueOf(worldFloorNum - TowerConsts.LOBBY_FLOOR);
      if (worldFloorNum == TowerConsts.LOBBY_FLOOR) {
        labelText = "L";
      } else if (worldFloorNum < TowerConsts.LOBBY_FLOOR) {
        labelText = "B" + (TowerConsts.LOBBY_FLOOR - worldFloorNum);
      }

      textBounds = floorFont.getBounds(labelText);
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.setScale(getGridScale());
      floorFont.draw(spriteBatch, labelText, worldPosition.x + ((TowerConsts.GRID_UNIT_SIZE - textBounds.width) / 2), worldPosition.y + (scaledGridUnit() * localFloorNum) + ((TowerConsts.GRID_UNIT_SIZE - textBounds.height) / 2));
    }

    elevatorCar.setColor(renderColor);
    elevatorCar.draw(spriteBatch);

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
    } else if (bottomSprite.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
      selectedResizeHandle = ResizeHandle.BOTTOM;
      anchorPoint = position.cpy().add(size);
    } else {
      selectedResizeHandle = null;
    }

    return selectedResizeHandle != null;
  }

  @Override
  public boolean touchUp() {
    if (selectedResizeHandle != null) {
      broadcastEvent(new ElevatorHeightChangeEvent(this));
    }

    selectedResizeHandle = null;

    return false;
  }

  @Override
  public boolean pan(GridPoint gridPointAtFinger, GridPoint gridPointDelta) {
    GridPoint newSize = size.cpy();
    GridPoint newPosition = position.cpy();

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

  public ElevatorCar getCar() {
    return elevatorCar;
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
    elevatorCar.clearQueue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Elevator)) return false;

    Elevator elevator = (Elevator) o;

    return !(elevatorCar != null ? !elevatorCar.equals(elevator.elevatorCar) : elevator.elevatorCar != null);
  }

  @Override
  public int hashCode() {
    return elevatorCar != null ? elevatorCar.hashCode() : 0;
  }
}
