package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tweenable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameObject implements Tweenable {
  protected Vector2 position;
  protected Vector2 velocity;
  protected Vector2 origin;
  protected Vector2 size;
  protected float scale;

  protected Sprite sprite;
  protected float rotation;
  private boolean visible;
  private float alpha;

  public GameObject(float x, float y, float scale) {
    sprite = new Sprite();
    position = new Vector2();
    origin = new Vector2();
    size = new Vector2();
    velocity = new Vector2();
    visible = true;
    alpha = 1.0f;

    setPosition(x, y);
    setScale(scale);
  }

  public GameObject(float x, float y) {
    this(x, y, 1.0f);
  }

  public GameObject(Vector2 position, float scale) {
    this(position.x, position.y, scale);
  }

  public void setScale(float scale) {
    this.scale = scale;
    sprite.setScale(this.scale, this.scale);
  }

  public Vector2 getPosition() {
    return position;
  }

  public void setPosition(float x, float y) {
    setPosition(new Vector2(x, y));
  }

  public void setPosition(Vector2 vec) {
    position.set(vec);
  }

  public void setSize(float width, float height) {
    size.set(width, height);
    origin.set(size);
    origin.mul(0.5f);

    sprite.setOrigin(origin.x, origin.y);
    sprite.setSize(size.x, size.y);
  }

  public Vector2 getSize() {
    return size;
  }

  public void useTexture(String filename) {
    Texture texture = new Texture(Gdx.files.internal(filename));

    sprite.setTexture(texture);
    sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());

    setSize(sprite.getWidth(), sprite.getHeight());
  }

  public void render(SpriteBatch batch) {
    if (visible) {
      sprite.draw(batch, alpha);
    }
  }

  public void update(float timeDelta) {
    position.add(velocity.x * timeDelta, velocity.y * timeDelta);
    sprite.setPosition(position.x - origin.x, position.y - origin.y);
    sprite.setRotation(rotation);
  }

  public Vector2 getOrigin() {
    return origin;
  }

  public float getScale() {
    return scale;
  }

  public void setSprite(Sprite sprite) {
    sprite.setPosition(position.x, position.y);
    sprite.setScale(scale);
    sprite.setOrigin(origin.x, origin.y);

    this.sprite = sprite;
  }

  public void setVisible(boolean state) {
    visible = state;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVelocity(int x, int y) {
    velocity.set(x, y);
  }

  public void setOpacity(float newAlpha) {
    alpha = newAlpha;
  }

  public int getTweenValues(int tweenType, float[] returnValues) {
    switch (tweenType) {
      case TWEEN_OPACITY:
        returnValues[0] = alpha;
        return 1;
    }

    return 0;
  }

  public void onTweenUpdated(int tweenType, float[] newValues) {
//    System.out.println("tweenType = " + tweenType + " newValues = " + Arrays.toString(newValues));
    switch (tweenType) {
      case TWEEN_OPACITY:
        setOpacity(newValues[0]);
        break;
    }
  }

  public static final int TWEEN_OPACITY = 0;
}
