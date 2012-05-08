/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.controllers.AvatarState;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.controllers.PathSearchManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;
import com.happydroids.droidtowers.pathfinding.WanderPathFinder;
import com.happydroids.droidtowers.utils.Random;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

import static com.happydroids.droidtowers.math.Direction.LEFT;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  public static final float WALKING_ANIMATION_DURATION = FRAME_DURATION * 3;
  private static final float PATH_SEARCH_DELAY = 0f;
  private static final Set<Color> colors = Sets.newHashSet(Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);

  private static Iterator colorIterator;
  private final Animation walkAnimation;

  private float walkAnimationTime;

  private AvatarSteeringManager steeringManager;
  protected final GameGrid gameGrid;
  private boolean isEmployed;
  private boolean isResident;
  private float satisfactionShops;
  private float satisfactionFood;
  private Color myColor;
  private float lastPathFinderSearch;
  protected GridObject movingTo;
  private TransitPathFinder pathFinder;
  private boolean justWandered;
  private Room home;
  private float hungerLevel;

  public Avatar(AvatarLayer avatarLayer) {
    super();

    gameGrid = avatarLayer.getGameGrid();

    setPosition(-Random.randomInt(50, 200), TowerConsts.GROUND_HEIGHT);

    pickColor();

    TextureAtlas droidAtlas = getTextureAtlas();
    TextureAtlas.AtlasRegion stationary = droidAtlas.findRegion(addFramePrefix("stationary"));
    setSize(stationary.originalWidth, stationary.originalHeight);
    setRegion(stationary);

    walkAnimation = new Animation(FRAME_DURATION, droidAtlas.findRegions(addFramePrefix("walk")));
    walkAnimationTime = 0f;
  }

  protected String addFramePrefix(String frameName) {
    return "droid/" + frameName;
  }

  protected TextureAtlas getTextureAtlas() {
    return TowerAssetManager.textureAtlas("characters.txt");
  }

  public void beginNextAction() {
    GuavaSet<GridObject> objects = gameGrid.getObjects();
    if (objects != null) {
      objects = objects.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject gridObject) {
          return gridObject instanceof Room && ((Room) gridObject).isConnectedToTransport();
        }
      });

      if (objects.size() > 0) {
        navigateToGridObject(objects.randomEntry());
      }
    } else {
      wanderAround();
    }
  }

  protected void wanderAround() {
    lastPathFinderSearch = 0f;
    GridPosition start = gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY()));

    setupPathFinder(new WanderPathFinder(gameGrid, start));
  }

  protected void navigateToGridObject(GridObject gridObject) {
    if (gridObject == null) {
      wanderAround();
      return;
    }
    movingTo = gridObject;

    GridPosition start = gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY()));
    GridPosition goal = gameGrid.positionCache().getPosition(gridObject.getPosition());

    setupPathFinder(new TransitPathFinder(gameGrid, start, goal));
  }

  private void setupPathFinder(final TransitPathFinder finder) {
    pathFinder = finder;
    justWandered = pathFinder instanceof WanderPathFinder;
    pathFinder.setCompleteCallback(new Runnable() {
      public void run() {
        createSteeringManagerFromPath();
      }
    });

    PathSearchManager.instance().queue(pathFinder);
  }

  private void createSteeringManagerFromPath() {
    if (pathFinder != null && pathFinder.wasSuccessful()) {
      steeringManager = new AvatarSteeringManager(this, gameGrid, pathFinder.getDiscoveredPath());
      steeringManager.setCompleteCallback(new Runnable() {
        public void run() {
          afterReachingTarget();
        }
      });
      steeringManager.start();
    }

    pathFinder = null;
  }

  private void afterReachingTarget() {
    if (movingTo instanceof CommercialSpace) {
      CommercialSpace.class.cast(movingTo).recordVisitor(this);
    }

    if (!justWandered) {
      wanderAround();
    }
    movingTo = null;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    hungerLevel -= 0.001f * timeDelta;

    lastPathFinderSearch += timeDelta;

    if (steeringManager != null) {
      if (steeringManager.isRunning()) {
        Set<AvatarState> steeringState = steeringManager.getCurrentState();
        if (steeringState.contains(AvatarState.MOVING) && !steeringState.contains(AvatarState.USING_ELEVATOR)) {
          walkAnimationTime += timeDelta;
          if (walkAnimationTime >= WALKING_ANIMATION_DURATION) {
            walkAnimationTime = 0f;
          }

          TextureRegion keyFrame = walkAnimation.getKeyFrame(walkAnimationTime, true);
          setRegion(keyFrame);
          flip(steeringManager.horizontalDirection() == LEFT, false);
        }
      } else {
        steeringManager = null;
      }
    } else if (pathFinder == null) {
      if (lastPathFinderSearch >= PATH_SEARCH_DELAY) {
        lastPathFinderSearch = 0f;

        beginNextAction();
      }
    }
  }

  protected void pickColor() {
    if (colorIterator == null || !colorIterator.hasNext()) {
      colorIterator = colors.iterator();
    }

    setColor((Color) colorIterator.next());
  }

  public void cancelMovement() {
    if (steeringManager != null) {
      steeringManager.cancel();
    }

    if (pathFinder != null) {
      PathSearchManager.instance().remove(pathFinder);
    }

    lastPathFinderSearch = PATH_SEARCH_DELAY;
    steeringManager = null;
    pathFinder = null;
  }

  public void murderDeathKill187() {
    cancelMovement();
    markToRemove(true);
  }

  public void setHome(GridObject newHome) {
    home = (Room) newHome;
    home.setResident(this);
    setPosition(home.getWorldCenterBottom());
  }
}
