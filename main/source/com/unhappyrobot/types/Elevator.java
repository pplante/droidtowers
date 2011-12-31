package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;

public class Elevator extends GridObject {
  private Sprite sprite;
  private final BitmapFont floorFont;

  public Elevator(ElevatorType elevatorType, GameGrid gameGrid) {
    super(elevatorType, gameGrid);

    sprite = new Sprite(new Texture(Gdx.files.internal("tiles/elevator-shaft.png")));
    floorFont = new BitmapFont(Gdx.files.internal("fonts/bank_gothic_64.fnt"), Gdx.files.internal("fonts/bank_gothic_64.png"), false);
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  public void render(SpriteBatch spriteBatch) {
    super.render(spriteBatch);


    for (int y = 0; y < size.y; y++) {
      String labelText = String.format("%d", y + 1);
      BitmapFont.TextBounds textBounds = floorFont.getBounds(labelText);
      floorFont.draw(spriteBatch, labelText, position.x * gameGrid.unitSize.x + ((sprite.getTexture().getWidth() - textBounds.width) / 2), ((position.y + y + 0.75f) * gameGrid.unitSize.y));
    }
  }
}
