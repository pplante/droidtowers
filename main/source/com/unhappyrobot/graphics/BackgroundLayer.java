package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GridObject;

import static com.badlogic.gdx.graphics.Texture.TextureWrap;

public class BackgroundLayer extends GridObject {
  private Texture texture;

  public BackgroundLayer(String fileName) {
    texture = new Texture(Gdx.files.internal(fileName));
    texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    position = new Vector2(1, 1);
    size = new Vector2(5, 5);
  }

  @Override
  public void render(SpriteBatch spriteBatch, Camera camera) {
  }

  @Override
  public Texture getTexture() {
    return texture;
  }
}
