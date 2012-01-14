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
  private static Texture roomDecals;
  private Sprite sprite;
  private Sprite decalSprite;

  private boolean dynamicSprite;
  private static final int UPDATE_FREQUENCY = 10000;
  private long lastUpdateTime;
  protected int currentResidency;
  private int populationRequired;
  private boolean connectedToTransport;

  public Room(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);

    connectedToTransport = roomType.isLobby();

    if (labelFont == null) {
      labelFont = new BitmapFont(Gdx.files.internal("fonts/helvetica_neue_18.fnt"), false);
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

    if (roomDecals == null) {
      roomDecals = new Texture(Gdx.files.internal("decals.png"));
    }

    decalSprite = new Sprite(roomDecals);
  }

  public void updatePopulation() {
    currentResidency = 0;

    if (isConnectedToTransport()) {
      int maxPopulation = ((RoomType) getGridObjectType()).getPopulationMax();
      if (maxPopulation > 0) {
        currentResidency = Random.randomInt(maxPopulation / 2, maxPopulation);
      }
    }
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    super.render(spriteBatch);

    if (!connectedToTransport) {
      decalSprite.setPosition(sprite.getX(), sprite.getY());
      decalSprite.draw(spriteBatch);
    }

    if (dynamicSprite) {
      BitmapFont.TextBounds textBounds = labelFont.getBounds(gridObjectType.getName());
      Vector2 centerPoint = size.toWorldVector2().sub(textBounds.width, textBounds.height).mul(0.5f);

      labelFont.draw(spriteBatch, gridObjectType.getName(), position.getWorldX() + centerPoint.x, position.getWorldY() + centerPoint.y);
    }
  }

  public int getCurrentResidency() {
    return currentResidency;
  }

  @Override
  public int getCoinsEarned() {
    if (currentResidency > 0) {
      RoomType roomType = (RoomType) gridObjectType;
      return (roomType.getCoinsEarned() / roomType.getPopulationMax()) * currentResidency;
    }

    return 0;
  }


  public void setConnectedToTransport(boolean connectedToTransport) {
    this.connectedToTransport = connectedToTransport;
  }

  public boolean isConnectedToTransport() {
    return connectedToTransport && placementState.equals(GridObjectPlacementState.PLACED);
  }

  @Override
  public float getNoiseLevel() {
    if (((RoomType) gridObjectType).getPopulationMax() > 0) {
      return super.getNoiseLevel() * (currentResidency / ((RoomType) gridObjectType).getPopulationMax());
    }

    return 0;
  }
}
