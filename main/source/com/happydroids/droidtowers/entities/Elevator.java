/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.actions.Action;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.ElevatorType;
import com.happydroids.droidtowers.types.ResizeHandle;

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

  public Elevator(ElevatorType elevatorType, final GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    if (elevatorAtlas == null) {
      elevatorAtlas = TowerAssetManager.textureAtlas(elevatorType.getAtlasFilename());
    }

    size.set(1, 3);
    topSprite = elevatorAtlas.createSprite("elevator/top");
    topSprite.setScale(getGridScale());
    bottomSprite = elevatorAtlas.createSprite("elevator/bottom");
    shaftSprite = elevatorAtlas.createSprite("elevator/shaft");
    emptyShaftSprite = elevatorAtlas.createSprite("elevator/empty");
    floorFont = TowerAssetManager.bitmapFont("fonts/bank_gothic_32.fnt");
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
    Vector2 renderPosition = position.cpy();

    Vector2 localPoint = worldPosition.cpy();

    if (selectedResizeHandle == ResizeHandle.BOTTOM) {
      bottomSprite.setColor(Colors.ICS_BLUE);
    } else {
      bottomSprite.setColor(renderColor);
    }
    bottomSprite.setPosition(localPoint.x, localPoint.y);
    bottomSprite.draw(spriteBatch);

    Sprite shaftToRender = drawShaft ? shaftSprite : emptyShaftSprite;
    BitmapFont.TextBounds textBounds;
    shaftToRender.setColor(renderColor);
    for (int y = (int) position.y + 1; y < position.y + size.y - 1; y++) {
      localPoint.add(0, scaledGridUnit());

      shaftToRender.setPosition(localPoint.x, localPoint.y);
      shaftToRender.draw(spriteBatch);

      String labelText = String.valueOf(y - TowerConsts.LOBBY_FLOOR + 1);
      if (y == TowerConsts.LOBBY_FLOOR) {
        labelText = "L";
      } else if (y < TowerConsts.LOBBY_FLOOR) {
        labelText = "B" + (TowerConsts.LOBBY_FLOOR - y);
      }

      textBounds = floorFont.getBounds(labelText);
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.draw(spriteBatch, labelText, localPoint.x + ((TowerConsts.GRID_UNIT_SIZE - textBounds.width) / 2), localPoint.y + ((TowerConsts.GRID_UNIT_SIZE - textBounds.height) / 2));
    }

    elevatorCar.setColor(renderColor);
    elevatorCar.draw(spriteBatch);

    localPoint.add(0, 1);
    if (selectedResizeHandle == ResizeHandle.TOP) {
      topSprite.setColor(Colors.ICS_BLUE);
    } else {
      topSprite.setColor(renderColor);
    }
    topSprite.setPosition(localPoint.x, localPoint.y);
    topSprite.draw(spriteBatch);
  }

  private float scaledGridUnit() {
    return TowerConsts.GRID_UNIT_SIZE * getGridScale();
  }

  @Override
  public boolean tap(Vector2 gridPointAtFinger, int count) {
    if (count >= 2) {
      drawShaft = !drawShaft;
    }

    return true;
  }

  @Override
  public boolean touchDown(Vector2 gameGridPoint) {
    if (gameGridPoint.y == position.y + size.y - 1) {
      selectedResizeHandle = ResizeHandle.TOP;
    } else if (gameGridPoint.y == position.y) {
      selectedResizeHandle = ResizeHandle.BOTTOM;
    } else {
      selectedResizeHandle = null;
    }

    return selectedResizeHandle != null;
  }

  @Override
  public boolean touchUp() {
    selectedResizeHandle = null;

    return false;
  }

  @Override
  public boolean pan(Vector2 gridPointAtFinger, Vector2 gridPointDelta) {
    GridPoint prevSize = size.cpy();
    GridPoint prevPosition = position.cpy();

    float newSize = -1;
    float newPosY = -1;
    if (selectedResizeHandle == ResizeHandle.TOP) {
      newSize = Math.max(gridPointDelta.y - position.y, 3);
    } else if (selectedResizeHandle == ResizeHandle.BOTTOM) {
      newPosY = gridPointDelta.y;
      newSize = position.y + size.y - gridPointDelta.y;
    }

    if (newSize < 3 || newSize > 17) {
      return true;
    }

    newSize = Math.max(newSize, 3);
    newSize = Math.min(newSize, 17);

    if (newSize != -1 && newSize != size.y) {
      size.y = newSize;
    }

    if (newPosY != -1) {
      position.y = Math.max(newPosY, 0);
    }

    broadcastEvent(new ElevatorHeightChangeEvent(this, prevSize, prevPosition));

    return true;
  }

  @Override
  public GridPoint getContentSize() {
    GridPoint cpy = size.cpy();
    cpy.sub(0, 3);
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
}
