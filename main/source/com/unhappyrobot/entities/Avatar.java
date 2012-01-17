package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.utils.Random;

import java.util.Set;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  private final Animation animation;
  private float animationTime;
  private boolean isMoving;
  private boolean isFlipped;
  private final GameGrid gameGrid;

  public Avatar(GameGrid gameGrid) {
    super();
    this.gameGrid = gameGrid;

    setPosition(Random.randomInt(0, gameGrid.getWorldSize().x), 256);

    TextureAtlas droidAtlas = new TextureAtlas(Gdx.files.internal("characters/droid.txt"));

    setSprite(droidAtlas.createSprite("stationary"));

    animation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    animationTime = 0f;
  }

  private void setupNewMovement() {
    isMoving = true;
    int newX = Random.randomInt(0, gameGrid.getWorldSize().x);
    getSprite().setColor(Color.WHITE);
    Set<GridObject> stairs = gameGrid.getInstancesOf(Stair.class);

    if (stairs != null) {
      for (GridObject gridObject : stairs) {
        Stair stair = (Stair) gridObject;

        float distanceToStair = Math.abs(stair.getPosition().toWorldVector2(gameGrid).dst(position));
        if (distanceToStair > 500 && distanceToStair <= 1000f) {
          System.out.println("distanceToStair = " + distanceToStair);
          newX = (int) stair.getPosition().getWorldX(gameGrid);
          getSprite().setColor(Color.PINK);
        }
      }
    }

    isFlipped = newX < position.x;

    int moveSpeed = (int) (Math.abs(position.x - newX) / 0.03f);
    Tween.to(this, TWEEN_POSITION, moveSpeed, Linear.INOUT).target(newX).addToManager(TowerGame.getTweenManager()).addCompleteCallback(new TweenCallback() {
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
