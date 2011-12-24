package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GridObject;

import static com.badlogic.gdx.graphics.Texture.TextureWrap;

public class BackgroundLayer extends GridObject {
  private final Sprite sprite;

  public BackgroundLayer(String fileName) {
    Texture texture = new Texture(Gdx.files.internal(fileName));
    texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

    sprite = new Sprite(texture);

    position = new Vector2(1, 1);
    size = new Vector2(5, 5);
  }

  @Override
  public Sprite getSprite() {
    return sprite;
  }
}
