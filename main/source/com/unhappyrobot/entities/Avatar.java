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
import com.google.common.collect.Lists;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.pathfinding.TransitPathFinder;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.LinkedList;
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
  private TransitPathFinder pathFinder;

  public Avatar(AvatarLayer avatarLayer) {
    super();
    this.gameGrid = avatarLayer.getGameGrid();

    setPosition(Random.randomInt(1792, 2560), 256);

    TextureAtlas droidAtlas = new TextureAtlas(Gdx.files.internal("characters/droid.txt"));

    setSprite(droidAtlas.createSprite("stationary"));
    size.set(getSprite().getWidth(), getSprite().getHeight());
    animation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    animationTime = 0f;
  }

  private void setupNewMovement() {
    isMoving = true;
    int newX = Random.randomInt(1792, 2560);
    isFlipped = newX < position.x;

    moveHorizontallyTo(newX);
  }

  public void findCommercialSpace() {
    gameGrid.getRenderer().setTransitLine(null);

    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      for (GridObject space : commercialSpaces) {
        space.setRenderColor(Color.WHITE);
      }

      ArrayList<GridObject> commercials = Lists.newArrayList(commercialSpaces);
//      new TransportCalculator(gameGrid, 0).run();
      GridObject commercialSpace = commercials.get(Random.randomInt(commercials.size()));
      commercialSpace.setRenderColor(Color.ORANGE);
      System.out.println("commercials = " + commercialSpace);
      pathFinder = new TransitPathFinder(commercialSpace.getPosition());
      pathFinder.compute(gameGrid.closestGridPoint(position.x, position.y));
    }
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

    if (pathFinder != null) {
      if (!pathFinder.isWorking()) {
        LinkedList<GridPoint> discoveredPath = pathFinder.getDiscoveredPath();
        if (discoveredPath != null) {
          System.out.println("discoveredPath = " + discoveredPath);
          TransitLine transitLine = new TransitLine(gameGrid);
          for (GridPoint gridPoint : discoveredPath) {
            transitLine.addPoint(gridPoint.toWorldVector2(gameGrid));
          }

          gameGrid.getRenderer().setTransitLine(transitLine);
        }

        System.out.println("pathFinder.getCost() = " + pathFinder.getCost());
        System.out.println("pathFinder.getExpandedCounter() = " + pathFinder.getExpandedCounter());

        pathFinder = null;
      } else {
//        System.out.println("pathFinder.getCost() = " + pathFinder.getCost());
//        System.out.println("pathFinder.getExpandedCounter() = " + pathFinder.getExpandedCounter());
        pathFinder.step();
      }
    }
  }

  public Sprite getSprite() {
    return sprite;
  }


}
