/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
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
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.pathfinding.TransitPathFinder;
import com.happydroids.droidtowers.utils.Random;

import javax.annotation.Nullable;
import java.util.*;

import static com.happydroids.droidtowers.types.ProviderType.FOOD;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  public static final float WALKING_ANIMATION_DURATION = FRAME_DURATION * 3;
  private static final float PATH_SEARCH_DELAY = 0f;
  private static final Set<Color> colors = Sets.newHashSet(Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);
  private static Iterator<Color> colorIterator = Iterables.cycle(colors).iterator();
  private final Animation walkAnimation;

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
  private Room home;
  private float hungerLevel;
  private LinkedList<Object> lastVisitedPlaces;
  private float lastSearchedForHome = Float.MAX_VALUE;
  private boolean wanderingAround;
  private final String name;


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
    steeringManager = new AvatarSteeringManager(this, this.gameGrid, null);
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
    wanderAround();

    if (!pathFinder.isWorking()) {
      if (hungerLevel <= 0.5f) {
        GridObject closestFood = searchForFood();
        navigateToGridObject(closestFood);
      } else {
        if (!lastVisitedPlaces.contains(home)) {
          navigateToGridObject(home);
        } else {
          findPlaceToVisit();
        }
      }
    }
  }

  protected void findPlaceToVisit() {
    ArrayList<GridObject> anyRoom = gameGrid.getInstancesOf(Room.class);
    if (!anyRoom.isEmpty()) {
      navigateToGridObject(anyRoom.get(Random.randomInt(anyRoom.size() - 1)));
    }
  }

  protected void wanderAround() {
    lastPathFinderSearch = 0f;
    GridPosition start = gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY()));

    LinkedList<GridPosition> discoveredPath = Lists.newLinkedList();

    GridPoint gridSize = gameGrid.getGridSize();

    if (start.y == TowerConsts.LOBBY_FLOOR) {
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
    } else {
      for (int i = 1; i < 5; i++) {
        GridPosition positionRight = gameGrid.positionCache().getPosition(start.x + i, start.y);
        if (positionRight != null && positionRight.size() > 0) {
          discoveredPath.add(positionRight);
        } else {
          break;
        }
      }

      for (int i = 1; i < 5; i++) {
        GridPosition positionLeft = gameGrid.positionCache().getPosition(start.x - i, start.y);
        if (positionLeft != null && positionLeft.size() > 0) {
          discoveredPath.add(positionLeft);
        } else {
          break;
        }
      }

      List<GridPosition> positions = Lists.newArrayList(discoveredPath);
      int numPositions = positions.size();
      for (int i = 0; i < numPositions / 4; i++) {
        discoveredPath.add(positions.get(Random.randomInt(numPositions - 1)));
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
      movingTo.getVisitorQueue().add(this);
    }

    GridPosition start = gameGrid.positionCache().getPosition(gameGrid.closestGridPoint(getX(), getY()));
    GridPosition goal = gameGrid.positionCache().getPosition(gridObject.getPosition());

    pathFinder.setStart(start);
    pathFinder.setGoal(goal);
    pathFinder.start();

    PathSearchManager.instance().queue(pathFinder);
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
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    if (home == null) {
      lastSearchedForHome += timeDelta;
      if (lastSearchedForHome > 10f) {
        lastSearchedForHome = 0f;
        searchForAHome();
      }
    }

    hungerLevel -= 0.001f * timeDelta;

    lastPathFinderSearch += timeDelta;

    if (!steeringManager.isRunning()) {
      beginNextAction();
    }
  }

  private GridObject searchForFood() {
    ArrayList<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces.isEmpty()) {
      return null;
    }


    final int avatarX = (int) Avatar.this.getX();
    final int avatarY = (int) Avatar.this.getY();

    GridObject closest = null;
    int closestDist = Integer.MAX_VALUE;
    for (int i = 0, commercialSpacesSize = commercialSpaces.size(); i < commercialSpacesSize; i++) {
      GridObject commercialSpace = commercialSpaces.get(i);
      if (commercialSpace.getVisitorQueue().size() >= 5) {
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

  private void searchForAHome() {
    List<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    if (rooms != null) {
      GridObject mostDesirable = rooms.get(0);
      for (int i = 0, roomsSize = rooms.size(); i < roomsSize; i++) {
        GridObject gridObject = rooms.get(i);
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

  public void cancelMovement() {
    if (steeringManager != null) {
      steeringManager.finished();
    }

    if (pathFinder != null) {
      PathSearchManager.instance().remove(pathFinder);
    }

    if (movingTo != null) {
      movingTo.getVisitorQueue().remove(this);
    }

    lastPathFinderSearch = PATH_SEARCH_DELAY;
  }

  public void setHome(GridObject newHome) {
    home = (Room) newHome;

    if (home != null) {
      home.addResident(this);
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
}
