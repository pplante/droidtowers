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

public class Elevator extends GridObject {
  private Sprite topSprite;
  private Sprite shaftSprite;
  private Sprite bottomSprite;
  private final Texture shaftTexture;
  private TextureAtlas elevatorAtlas;
  private final BitmapFont floorFont;

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
    topSprite.setPosition(gameGrid.gridOrigin.x + position.x * gameGrid.unitSize.x, (gameGrid.gridOrigin.y + position.y + size.y - 1) * gameGrid.unitSize.y);
    topSprite.setSize(gameGrid.unitSize.x, gameGrid.unitSize.y);
    topSprite.draw(spriteBatch);

    bottomSprite.setPosition(gameGrid.gridOrigin.x + position.x * gameGrid.unitSize.x, (gameGrid.gridOrigin.y + position.y) * gameGrid.unitSize.y);
    bottomSprite.setSize(gameGrid.unitSize.x, gameGrid.unitSize.y);
    bottomSprite.draw(spriteBatch);

    for (int y = 1; y < size.y - 1; y++) {
      shaftSprite.setPosition(gameGrid.gridOrigin.x + position.x * gameGrid.unitSize.x, (gameGrid.gridOrigin.y + position.y + y) * gameGrid.unitSize.y);
      shaftSprite.setSize(size.x * gameGrid.unitSize.x, 1 * gameGrid.unitSize.y);
      shaftSprite.draw(spriteBatch);

      String labelText = String.format("%d", y);
      BitmapFont.TextBounds textBounds = floorFont.getBounds(labelText);
      floorFont.setColor(1, 1, 1, 0.5f);
      floorFont.draw(spriteBatch, labelText, position.x * gameGrid.unitSize.x + ((shaftTexture.getWidth() - textBounds.width) / 2), ((position.y + y + 0.75f) * gameGrid.unitSize.y));
    }
  }

  @Override
  public boolean tap(Vector2 gridPointAtFinger, int count) {
    System.out.println("gridPointAtFinger = " + gridPointAtFinger);
    System.out.println("getBounds() = " + getBounds());
    if (gridPointAtFinger.y == position.y + size.y - 1) {
      size.y += 1;

      return true;
    } else if (gridPointAtFinger.y == position.y) {
      size.y += 1;
      position.y -= 1;
    }

    return false;
  }
}
