package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.types.RoomType;
import com.unhappyrobot.utils.Random;

public class Room extends GridObject {
  private static TextureAtlas roomAtlas;
  private static BitmapFont labelFont;
  private Sprite sprite;
  private boolean dynamicSprite;
  private long lastPopulationUpdateTime;
  private static final int POPULATION_CHANGE_FREQUENCY = 10000;
  private int currentPopulation;

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
  public void render(SpriteBatch spriteBatch) {
    super.render(spriteBatch);

    if (dynamicSprite) {
      BitmapFont.TextBounds textBounds = labelFont.getBounds(gridObjectType.getName());
      Vector2 centerPoint = size.toWorldVector2().sub(textBounds.width, textBounds.height).mul(0.5f);

      labelFont.draw(spriteBatch, gridObjectType.getName(), position.getWorldX() + centerPoint.x, position.getWorldY() + centerPoint.y);
    }
  }

  @Override
  public void update(float deltaTime) {
    if (state.equals(GridObjectState.PLACED)) {
      if ((lastPopulationUpdateTime + POPULATION_CHANGE_FREQUENCY) < System.currentTimeMillis()) {
        lastPopulationUpdateTime = System.currentTimeMillis();
        int maxPopulation = ((RoomType) getGridObjectType()).getMaxPopulation();
        if (maxPopulation > 0) {
          currentPopulation = Random.randomInt(0, maxPopulation);
        }
      }
    }
  }

  public int getCurrentPopulation() {
    return currentPopulation;
  }

  @Override
  public int getCoinsEarned() {
    if (currentPopulation > 0) {
      RoomType roomType = (RoomType) gridObjectType;
      return (roomType.getCoinsEarned() / roomType.getMaxPopulation()) * currentPopulation;
    }

    return 0;
  }

  @Override
  public int getGoldEarned() {
    if (currentPopulation > 0) {
      RoomType roomType = (RoomType) gridObjectType;
      return (roomType.getGoldEarned() / roomType.getMaxPopulation()) * currentPopulation;
    }

    return 0;
  }
}
