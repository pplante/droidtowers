package com.unhappyrobot.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.unhappyrobot.entities.GameLayer;

import static com.badlogic.gdx.graphics.Texture.TextureWrap;

public class BackgroundLayer extends GameLayer {
  private final Sprite sprite;

  public BackgroundLayer(String fileName) {
    this(new Texture(Gdx.files.internal(fileName)));
  }

  public BackgroundLayer(Texture texture) {
    sprite = new Sprite(texture);
  }

  public void setPosition(int x, int y) {
    sprite.setPosition(x, y);
  }

  public void setSize(float w, float h) {
    sprite.setSize(w, h);
  }

  public void setWrap(TextureWrap wrapX, TextureWrap wrapY) {
    sprite.getTexture().setWrap(wrapX, wrapX);

    if (wrapX == TextureWrap.Repeat) {
      sprite.setU(0f);
      sprite.setU2(sprite.getWidth() / sprite.getTexture().getWidth());
    }

    if (wrapY == TextureWrap.Repeat) {
      sprite.setV(0f);
      sprite.setV2(sprite.getHeight() / sprite.getTexture().getHeight());
    }
  }

  @Override
  public void render(SpriteBatch spriteBatch, Camera camera) {
    spriteBatch.begin();
    sprite.draw(spriteBatch);
    spriteBatch.end();
  }
}
