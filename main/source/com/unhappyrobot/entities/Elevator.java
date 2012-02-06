package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.actions.Action;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GridObjectBoundsChangeEvent;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.ElevatorType;
import com.unhappyrobot.types.ResizeHandle;

public class Elevator extends Transit {
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite emptyShaftSprite;
  private Sprite bottomSprite;
  private Sprite carSprite;
  private final BitmapFont floorFont;
  private ResizeHandle selectedResizeHandle;
  private boolean drawShaft;
  private Action onResizeAction;

  public Elevator(ElevatorType elevatorType, final GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    size.set(1, 3);

    TextureAtlas elevatorAtlas = new TextureAtlas(Gdx.files.internal(elevatorType.getAtlasFilename()));

    topSprite = elevatorAtlas.createSprite("elevator/top");
    bottomSprite = elevatorAtlas.createSprite("elevator/bottom");
    shaftSprite = elevatorAtlas.createSprite("elevator/shaft");
    emptyShaftSprite = elevatorAtlas.createSprite("elevator/empty");
    carSprite = elevatorAtlas.createSprite("elevator/car");
    floorFont = new BitmapFont(Gdx.files.internal("fonts/bank_gothic_32.fnt"), false);
    drawShaft = true;
  }

  @Override
  public Sprite getSprite() {
    return shaftSprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    Vector2 renderPosition = position.cpy();

    GridPoint gridPoint = new GridPoint(position.x, position.y);

    bottomSprite.setColor(renderColor);
    bottomSprite.setPosition(gridPoint.getWorldX(gameGrid), gridPoint.getWorldY(gameGrid));
    bottomSprite.draw(spriteBatch);

    carSprite.setPosition(gridPoint.getWorldX(gameGrid), gridPoint.getWorldY(gameGrid));
    carSprite.draw(spriteBatch);

    Sprite shaftToRender = drawShaft ? shaftSprite : emptyShaftSprite;
    shaftToRender.setColor(renderColor);
    for (int y = (int) position.y + 1; y < position.y + size.y - 1; y++) {
      gridPoint.add(0, 1);

      shaftToRender.setPosition(gridPoint.getWorldX(gameGrid), gridPoint.getWorldY(gameGrid));
      shaftToRender.draw(spriteBatch);

      String labelText = (y < 4 ? "B" + (4 - y) : "" + (y - 3));
      BitmapFont.TextBounds textBounds = floorFont.getBounds(labelText);
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.draw(spriteBatch, labelText, gridPoint.getWorldX(gameGrid) + ((gameGrid.unitSize.x - textBounds.width) / 2), gridPoint.getWorldY(gameGrid) + ((gameGrid.unitSize.y - textBounds.height) / 2));
    }

    gridPoint.add(0, 1);
    topSprite.setColor(renderColor);
    topSprite.setPosition(gridPoint.getWorldX(gameGrid), gridPoint.getWorldY(gameGrid));
    topSprite.draw(spriteBatch);
  }

  @Override
  public boolean touchDown(Vector2 gameGridPoint) {
    if (gameGridPoint.y == position.y + size.y - 1) {
      selectedResizeHandle = ResizeHandle.TOP;
    } else if (gameGridPoint.y == position.y) {
      selectedResizeHandle = ResizeHandle.BOTTOM;
    } else {
      selectedResizeHandle = null;
      drawShaft = !drawShaft;
    }

    return selectedResizeHandle != null;
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

    GameEvents.post(new GridObjectBoundsChangeEvent(this, prevSize, prevPosition));

    return true;
  }

  @Override
  public Vector2 getContentSize() {
    return size.cpy().sub(0, 2);
  }

  @Override
  public Vector2 getContentPosition() {
    return position.cpy().add(0, 1);
  }
}
