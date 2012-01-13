package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.types.RoomType;
import com.unhappyrobot.utils.Random;

public class Room extends GridObject {
  private static TextureAtlas roomAtlas;
  private static BitmapFont labelFont;
  private Sprite sprite;
  private boolean dynamicSprite;

  private static final int UPDATE_FREQUENCY = 10000;
  private long lastUpdateTime;
  private int currentPopulation;
  private int populationRequired;

  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    if (labelFont == null) {
      labelFont = new BitmapFont(Gdx.files.internal("fonts/bank_gothic_32.fnt"), false);
    }

    if (roomType.getAtlasFilename() != null) {
      if (roomAtlas == null) {
        roomAtlas = new TextureAtlas(Gdx.files.internal(roomType.getAtlasFilename()));
      }
      sprite = new Sprite(roomAtlas.findRegion(roomType.getImageFilename()));
    } else {
      int width = (int) (gameGrid.unitSize.x * size.x);
      int height = (int) (gameGrid.unitSize.y * size.y);
      int pixmapSize = MathUtils.nextPowerOfTwo(Math.max(width, height));
      Pixmap pixmap = new Pixmap(pixmapSize, pixmapSize, Pixmap.Format.RGB565);
      pixmap.setColor(Color.BLACK);
      pixmap.fill();
      pixmap.setColor(Color.GRAY);
      pixmap.fillRectangle(1, 1, width - 2, height - 2);

      Texture texture = new Texture(pixmap, true);
      texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
      texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

      TextureRegion textureRegion = new TextureRegion(texture, 0, 0, width, height);

      sprite = new Sprite(textureRegion);
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
    if (shouldUpdate()) {
      int maxPopulation = ((RoomType) getGridObjectType()).getMaxPopulation();
      if (maxPopulation > 0) {
        currentPopulation = Random.randomInt(0, maxPopulation);
      }
    }
  }

  protected boolean shouldUpdate() {
    if (placementState.equals(GridObjectPlacementState.PLACED)) {
      if ((lastUpdateTime + UPDATE_FREQUENCY) < System.currentTimeMillis()) {
        lastUpdateTime = System.currentTimeMillis();
        return true;
      }
    }

    return false;
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
