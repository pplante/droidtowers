package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.utils.Random;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  private final Animation animation;
  private float animationTime;
  private boolean isMoving;
  private boolean isFlipped;
  private final Vector2 worldSize;

  public Avatar(Vector2 worldSize) {
    super();
    this.worldSize = worldSize;

    setPosition(Random.randomInt(0, worldSize.x), 256);

    TextureAtlas droidAtlas = new TextureAtlas(Gdx.files.internal("characters/droid.txt"));

    setSprite(droidAtlas.createSprite("stationary"));

    animation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    animationTime = 0f;
  }

  private void setupNewMovement() {
    isMoving = true;

    int newX = Random.randomInt(0, worldSize.x);
    isFlipped = newX < position.x;

    int moveSpeed = (int) (Math.abs(position.x - newX) / 0.03f);
    Tween.to(this, TWEEN_POSITION, moveSpeed, Quad.INOUT).target(newX).addToManager(TowerGame.getTweenManager()).addCompleteCallback(new TweenCallback() {
      public void tweenEventOccured(Types eventType, Tween tween) {
        isMoving = false;
      }
    });
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    animationTime += timeDelta;

    TextureRegion keyFrame = animation.getKeyFrame(animationTime, true);
    getSprite().setRegion(keyFrame);
    getSprite().flip(isFlipped, false);

    if (animationTime >= FRAME_DURATION * 3) {
      animationTime = 0f;
    }

    if (!isMoving) {
      setupNewMovement();
    }
  }

  public Sprite getSprite() {
    return sprite;
  }
}
