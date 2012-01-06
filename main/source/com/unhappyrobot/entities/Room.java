package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.RoomType;

public class Room extends GridObject {
  private static TextureAtlas roomAtlas;
  private static BitmapFont labelFont;
  private Sprite sprite;
  private boolean dynamicSprite;

  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    if (labelFont == null) {
      labelFont = new BitmapFont(Gdx.files.internal("fonts/bank_gothic_32.fnt"), Gdx.files.internal("fonts/bank_gothic_32.png"), false);
    }

    if (roomType.getAtlasFilename() != null) {
      if (roomAtlas == null) {
        roomAtlas = new TextureAtlas(Gdx.files.internal(roomType.getAtlasFilename()));
      }
      sprite = new Sprite(roomAtlas.findRegion(roomType.getImageFilename()));
    } else {
      int width = (int) (gameGrid.unitSize.x * size.x);
      int height = (int) (gameGrid.unitSize.y * size.y);
      Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
      pixmap.setColor(Color.BLACK);
      pixmap.fill();
      pixmap.setColor(Color.GRAY);
      pixmap.fillRectangle(1, 1, width - 2, height - 2);

      sprite = new Sprite(new Texture(pixmap));
      dynamicSprite = true;
    }
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public boolean canShareSpace() {
    return false;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    super.render(spriteBatch);

    if (dynamicSprite) {
      GridPoint gridPoint = new GridPoint(gameGrid, position);
      BitmapFont.TextBounds textBounds = labelFont.getBounds(gridObjectType.getName());
      labelFont.draw(spriteBatch, gridObjectType.getName(), gridPoint.getX() + ((gameGrid.gridScaleX(size.x) - textBounds.width) / 2), gridPoint.getY() + ((gameGrid.gridScaleY(size.y) - textBounds.height) / 2));
    }
  }
}
