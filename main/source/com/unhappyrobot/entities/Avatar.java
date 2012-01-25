package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.utils.Random;

import java.util.List;
import java.util.Set;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  private final Animation animation;
  private float animationTime;
  private boolean isMoving;
  private boolean isFlipped;
  private final GameGrid gameGrid;

  private boolean isEmployed;
  private boolean isResident;
  private float satisfactionShops;
  private float satisfactionFood;

  public Avatar(AvatarLayer avatarLayer) {
    super();
    this.gameGrid = avatarLayer.getGameGrid();

    setPosition(Random.randomInt(0, gameGrid.getWorldSize().x), 256);

    TextureAtlas droidAtlas = new TextureAtlas(Gdx.files.internal("characters/droid.txt"));

    setSprite(droidAtlas.createSprite("stationary"));

    animation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    animationTime = 0f;
  }

  private void setupNewMovement() {
    isMoving = true;
    int newX = Random.randomInt(0, gameGrid.getWorldSize().x);
    isFlipped = newX < position.x;

    moveHorizontallyTo(newX);

    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      final CommercialSpace firstCommercialSpace = (CommercialSpace) Iterables.get(commercialSpaces, Random.randomInt(commercialSpaces.size()), null);
      if (firstCommercialSpace != null) {
        List<GridObject> transitObjects = Lists.newArrayList(gameGrid.getInstancesOf(Stair.class, Elevator.class));
        List<GridObject> sortedTransitObjects = Lists.newArrayList();

        float currentFloor = firstCommercialSpace.getContentPosition().y;
        float originalFloor = currentFloor;
        GridObject transitClosestToLobby;
        do {
          transitClosestToLobby = findTransitClosestToLobby(transitObjects, currentFloor, originalFloor);
          if (transitClosestToLobby == null) {
            break;
          }
          sortedTransitObjects.add(transitClosestToLobby);
          transitObjects.remove(transitClosestToLobby);
          currentFloor = transitClosestToLobby.getContentPosition().y;
        } while (transitClosestToLobby.distanceToLobby() > 0);
      }
    }
  }

  private GridObject findTransitClosestToLobby(List<GridObject> transitObjects, final float floor, final float originalFloor) {
    List<GridObject> candidateObjects = Lists.newArrayList(transitObjects);

    Iterables.removeIf(candidateObjects, new Predicate<GridObject>() {
      public boolean apply(@Nullable GridObject gridObject) {
        TransitGridObject transitGridObject = (TransitGridObject) gridObject;
        return transitGridObject != null && !(transitGridObject.connectsToFloor(floor) && transitGridObject.connectsToFloor(floor - 1));
      }
    });

    Ordering<GridObject> gridObjectOrdering = Ordering.natural().onResultOf(new Function<GridObject, Float>() {
      public Float apply(@Nullable GridObject transitObject) {
        if (transitObject != null) {
          return transitObject.distanceFromFloor(originalFloor);
        }

        return Float.MIN_NORMAL;
      }
    });

    return Iterables.getFirst(gridObjectOrdering.sortedCopy(candidateObjects), null);
  }

  private void moveHorizontallyTo(int newX) {
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
