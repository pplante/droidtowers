/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.controllers.PathSearchManager;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.Direction;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;
import com.happydroids.droidtowers.utils.Random;
import com.happydroids.error.ErrorUtil;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static com.happydroids.droidtowers.controllers.AvatarState.MOVING;
import static com.happydroids.droidtowers.controllers.AvatarState.USING_STAIRS;
import static com.happydroids.droidtowers.types.ProviderType.COMMERCIAL;
import static com.happydroids.droidtowers.types.ProviderType.FOOD;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  public static final float WALKING_ANIMATION_DURATION = FRAME_DURATION * 3;
  private static final Set<Color> colors = Sets.newHashSet(Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);
  private static Iterator<Color> colorIterator = Iterables.cycle(colors).iterator();
  private final Animation walkAnimation;
  private float walkAnimationTime;

  private AvatarSteeringManager steeringManager;
  protected final GameGrid gameGrid;
  private boolean isEmployed;
  private boolean isResident;
  private float satisfactionShops;
  private float satisfactionFood;
  protected GridObject movingTo;
  private TransitPathFinder pathFinder;
  private Room home;
  private float hungerLevel;
  private LinkedList<Object> lastVisitedPlaces;
  private float lastSearchedForHome = Float.MAX_VALUE;
  private boolean wanderingAround;
  private final String name;
  private float timeUntilPathSearch;

  public Avatar(final GameGrid gameGrid) {
    super();

    this.gameGrid = gameGrid;
    name = NameGenerator.randomMaleName();

    setPosition(-Random.randomInt(50, 200), TowerConsts.GROUND_HEIGHT);

    setColor(colorIterator.next());

    TextureAtlas droidAtlas = getTextureAtlas();
    TextureAtlas.AtlasRegion stationary = droidAtlas.findRegion(addFramePrefix("stationary"));
    setSize(stationary.originalWidth, stationary.originalHeight);
    setRegion(stationary);
    setOrigin(stationary.originalWidth / 2, 0);
//    setVisible(false);

    walkAnimation = new Animation(FRAME_DURATION, droidAtlas.findRegions(addFramePrefix("walk")));
    lastVisitedPlaces = Lists.newLinkedList();
    satisfactionFood = 1f;
    satisfactionShops = 1f;

    pathFinder = new TransitPathFinder(this.gameGrid, this instanceof Janitor);
    pathFinder.setCompleteCallback(new Runnable() {
      @Override
      public void run() {
        pathFinderComplete();
      }
    });
    steeringManager = new AvatarSteeringManager(this, this.gameGrid);
  }

  private void pathFinderComplete() {
    if (pathFinder.isFinished()) {
      steeringManager.setPath(pathFinder.getDiscoveredPath());
      steeringManager.start();
    }
  }

  protected String addFramePrefix(String frameName) {
    return "droid/" + frameName;
  }

  protected TextureAtlas getTextureAtlas() {
    return TowerAssetManager.textureAtlas("characters.txt");
  }

  private void beginNextAction() {
    try {
      wanderAround();

      if (timeUntilPathSearch > 0) {
        return;
      }

      if (!pathFinder.isWorking()) {
        if (hungerLevel <= 0.5f) {
          GridObject closestFood = searchForFood();
          navigateToGridObject(closestFood);
        } else {
          if (home != null && !lastVisitedPlaces.contains(home)) {
            navigateToGridObject(home);
          } else {
            findPlaceToVisit();
          }
        }
      }
    } catch (Throwable throwable) {
      ErrorUtil.sendErrorToServer(throwable);
    }
  }

  protected void findPlaceToVisit() {
    Array<GridObject> gridObjects = gameGrid.getObjects();
    if (gridObjects.size == 1) {
      navigateToGridObject(gridObjects.get(0));
    } else if (gridObjects.size > 0) {
      int idx = 0;
      for (GridObject gridObject : gridObjects) {
        if (gridObject.provides(COMMERCIAL) && gridObject.getDirtLevel() < 1f) {
          navigateToGridObject(gridObject);
          break;
        }
      }
    }
  }

  protected void wanderAround() {
    GridPosition start = gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY()));

    Array<GridPosition> discoveredPath = new Array<GridPosition>(5);

    GridPoint gridSize = gameGrid.getGridSize();

    if (start.y == TowerConsts.LOBBY_FLOOR) {
      discoveredPath.add(gameGrid.positionCache()
          .getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
      discoveredPath.add(gameGrid.positionCache()
          .getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
    } else {
      for (int i = 1; i < 5; i++) {
        GridPosition positionRight = gameGrid.positionCache().getPosition(start.x + i, start.y);
        if (positionRight != null && !positionRight.isEmpty()) {
          if (positionRight.elevator == null) {
            discoveredPath.add(positionRight);
          }
        } else {
          break;
        }
      }

      for (int i = 1; i < 5; i++) {
        GridPosition positionLeft = gameGrid.positionCache().getPosition(start.x - i, start.y);
        if (positionLeft != null && !positionLeft.isEmpty()) {
          if (positionLeft.elevator == null) {
            discoveredPath.add(positionLeft);
          }
        } else {
          break;
        }
      }
    }

    steeringManager.setPath(discoveredPath);
    steeringManager.start();
  }

  protected void navigateToGridObject(GridObject gridObject) {
    cancelMovement();

    if (gridObject == null) {
      wanderAround();
      return;
    }
    movingTo = gridObject;

    if (movingTo != null) {
      movingTo.addToVisitorQueue(this);
    }

    pathFinder.setStart(gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY())));
    pathFinder.setGoal(gameGrid.positionCache().getPosition(gridObject.getPosition()));
    pathFinder.start();

    if (this instanceof Janitor) {
      while (pathFinder.isWorking()) {
        pathFinder.step();
      }

      pathFinder.runCompleteCallback();
    } else {
      PathSearchManager.instance().queue(pathFinder);
    }
  }

  public void afterReachingTarget() {
    if (movingTo != null) {
      movingTo.recordVisitor(this);
      lastVisitedPlaces.add(movingTo);
      if (lastVisitedPlaces.size() > 3) {
        lastVisitedPlaces.poll();
      }

      if (movingTo.provides(FOOD)) {
        hungerLevel = 1f;
      }
    }

    movingTo = null;
    timeUntilPathSearch = PathSearchManager.instance().queueLength() > 5 ? 5f + MathUtils.random(1, 5f) : 0f;
  }

  @Override
  public void update(float delta) {
    super.update(delta);

    timeUntilPathSearch -= delta;

    if (home == null) {
      lastSearchedForHome += delta;
      if (lastSearchedForHome > 10f) {
        lastSearchedForHome = 0f;
        searchForAHome();
      }
    }

    hungerLevel -= 0.001f * delta;

    if (!steeringManager.isRunning()) {
      beginNextAction();
    } else {
      if ((steeringManager.getCurrentState() & MOVING) != 0 || (steeringManager.getCurrentState() & USING_STAIRS) != 0) {
        walkAnimationTime += delta;
        if (walkAnimationTime >= WALKING_ANIMATION_DURATION) {
          walkAnimationTime = 0f;
        }

        TextureRegion keyFrame = walkAnimation.getKeyFrame(walkAnimationTime, true);
        setRegion(keyFrame);
        flip(steeringManager.horizontalDirection() == Direction.LEFT, false);
      }
    }
  }

  private GridObject searchForFood() {
    Array<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces.size == 0) {
      return null;
    }

    final int avatarX = (int) Avatar.this.getX();
    final int avatarY = (int) Avatar.this.getY();

    GridObject closest = null;
    int closestDist = Integer.MAX_VALUE;
    for (GridObject gridObject : commercialSpaces) {
      CommercialSpace commercialSpace = (CommercialSpace) gridObject;
      if (commercialSpace.getVisitorQueueSize() >= 5 || commercialSpace.getEmploymentLevel() > 0.0f) {
        continue;
      }

      int distanceFromAvatar = commercialSpace.getPosition().dst(avatarX, avatarY);
      if (distanceFromAvatar < closestDist) {
        closest = commercialSpace;
      }
    }

    if (lastVisitedPlaces.contains(closest) || closest == null) {
      satisfactionFood = MathUtils.clamp(satisfactionFood - 0.15f, 0f, 1f);
    } else {
      satisfactionFood = MathUtils.clamp(satisfactionFood + 0.15f, 0f, 1f);
    }

    return closest;
  }

  public void cancelMovement() {
    if (steeringManager != null) {
      steeringManager.finished();
    }

    if (pathFinder != null) {
      PathSearchManager.instance().remove(pathFinder);
    }

    if (movingTo != null) {
      movingTo.removeFromVisitorQueue(this);
    }

    timeUntilPathSearch = 5f + MathUtils.random(1, 5f);
  }

  public void searchForAHome() {
    if (home != null) {
      return;
    }

    Array<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    if (rooms != null && rooms.size > 0) {
      GridObject mostDesirable = rooms.get(0);
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        if (room.isConnectedToTransport() && room.getNumSupportedResidents() > 0) {
          if (room.getNumResidents() == 0 || (room.getNumResidents() < room.getNumSupportedResidents() && mostDesirable.getDesirability() < room.getDesirability())) {
            mostDesirable = room;
          }
        }
      }

      setHome(mostDesirable);
    }
  }

  public void setHome(GridObject newHome) {
    home = (Room) newHome;

    if (home != null && home.addResident(this)) {
      cancelMovement();
      setPosition(home.getWorldCenterBottom());
    }
  }

  public void recalculateCurrentPath() {
    cancelMovement();

    if (movingTo != null) {
      navigateToGridObject(movingTo);
    } else {
      wanderAround();
    }
  }


  public static final Predicate<GridObject> AVATAR_HOME_FILTER = new Predicate<GridObject>() {
    @Override
    public boolean apply(@Nullable GridObject input) {
      if (input instanceof Room) {
        Room room = (Room) input;
        return room.isConnectedToTransport() && (room.getNumResidents() == 0 || room.getNumResidents() < room.getNumSupportedResidents());
      }

      return false;
    }
  };

  @Override
  public void markToRemove(boolean b) {
    super.markToRemove(b);

    cancelMovement();
  }

  public String getName() {
    return name;
  }

  public GridObject getMovementTarget() {
    return movingTo;
  }

  public float getHungerLevel() {
    return hungerLevel;
  }

  public float getSatisfactionFood() {
    return satisfactionFood;
  }

  public float getSatisfactionShops() {
    return satisfactionShops;
  }

  public void murderDeathKill187() {
    cancelMovement();
    if (home != null) {
      home.removeResident(this);
    }
    markToRemove(true);
  }
}
