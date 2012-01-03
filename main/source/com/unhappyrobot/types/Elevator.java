package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

public class Elevator extends GridObject {
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite bottomSprite;
  private final Texture shaftTexture;
  private TextureAtlas elevatorAtlas;
  private final BitmapFont floorFont;
  private ResizeHandle selectedResizeHandle;

  public Elevator(ElevatorType elevatorType, GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    size.set(1, 3);

    elevatorAtlas = new TextureAtlas(Gdx.files.internal("tiles/elevator.txt"));

    topSprite = elevatorAtlas.createSprite("elevator-top");
    bottomSprite = elevatorAtlas.createSprite("elevator-bottom");
    shaftSprite = elevatorAtlas.createSprite("elevator-shaft");
    shaftTexture = shaftSprite.getTexture();
    shaftTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    floorFont = new BitmapFont(Gdx.files.internal("fonts/bank_gothic_64.fnt"), Gdx.files.internal("fonts/bank_gothic_64.png"), false);
  }

  @Override
  public Sprite getSprite() {
    return shaftSprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    Vector2 renderPosition = position.cpy();

    GridPoint gridPoint = new GridPoint(gameGrid, position);

    bottomSprite.setPosition(gridPoint.getX(), gridPoint.getY());
    bottomSprite.draw(spriteBatch);

    for (int y = (int) position.y + 1; y < position.y + size.y - 1; y++) {
      gridPoint.add(0, 1);

      shaftSprite.setPosition(gridPoint.getX(), gridPoint.getY());
      shaftSprite.draw(spriteBatch);

      String labelText = (y < 4 ? "B" + (4 - y) : "" + (y - 3));
      BitmapFont.TextBounds textBounds = floorFont.getBounds(labelText);
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.draw(spriteBatch, labelText, gridPoint.getX() + ((gameGrid.unitSize.x - textBounds.width) / 2), gridPoint.getY() + ((gameGrid.unitSize.y - textBounds.height) / 2));
    }

    gridPoint.add(0, 1);
    topSprite.setPosition(gridPoint.getX(), gridPoint.getY());
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
    }

    return selectedResizeHandle != null;
  }

  @Override
  public boolean pan(Vector2 gridPointAtFinger, Vector2 gridPointDelta) {
    if (selectedResizeHandle == ResizeHandle.TOP) {
      size.y = Math.max(gridPointDelta.y - position.y, 3);
    } else if (selectedResizeHandle == ResizeHandle.BOTTOM) {
      float newSize = Math.max(size.y + position.y - gridPointDelta.y, 3);
      if (newSize >= 3) {
        position.y = gridPointDelta.y;
        size.y = newSize;
      }
    }

    if (position.y < 0) {
      position.y = 0;
    }

    if (size.y > 17) {
      size.y = 17;
    }

    return true;
  }
}
