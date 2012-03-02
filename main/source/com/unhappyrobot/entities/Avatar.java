package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.AvatarState;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.controllers.PathSearchManager;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.SpeechBubble;
import com.unhappyrobot.pathfinding.TransitPathFinder;
import com.unhappyrobot.pathfinding.WanderPathFinder;
import com.unhappyrobot.utils.Random;

import java.util.Iterator;
import java.util.Set;

import static com.unhappyrobot.math.Direction.LEFT;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  public static final float WALKING_ANIMATION_DURATION = FRAME_DURATION * 3;
  private static final float PATH_SEARCH_DELAY = 25f;
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
  private final SpeechBubble speechBubble;
  private float lastPathFinderSearch;
  protected GridObject movingTo;

  public Avatar(AvatarLayer avatarLayer) {
    super();

    gameGrid = avatarLayer.getGameGrid();

    setPosition(-Random.randomInt(50, 200), 256);

    pickColor();

    TextureAtlas droidAtlas = getTextureAtlas();
    TextureAtlas.AtlasRegion stationary = droidAtlas.findRegion("stationary");
    setSize(stationary.originalWidth, stationary.originalHeight);
    setRegion(stationary);

    walkAnimation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    walkAnimationTime = 0f;

    speechBubble = new SpeechBubble();
    speechBubble.followObject(this);
    HeadsUpDisplay.instance().addActor(speechBubble);
  }

  protected TextureAtlas getTextureAtlas() {
    return new TextureAtlas(Gdx.files.internal("characters/droid.txt"));
  }

  private void displaySpeechBubble(String newText) {
    speechBubble.setText(newText);
    speechBubble.show();
  }

  public void beginNextAction() {
    GuavaSet<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      commercialSpaces = commercialSpaces.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).isConnectedToTransport();
        }
      });

      if (commercialSpaces.size() > 0) {
        navigateToGridObject(commercialSpaces.getRandomEntry());
      }
    } else {
      wanderAround();
    }
  }

  protected void wanderAround() {
    GridPosition start = GridPositionCache.instance().getPosition(gameGrid.closestGridPoint(getX(), getY()));
    System.out.println(String.format("%s is bored.", this.getClass().getSimpleName()));

    WanderPathFinder pathFinder = new WanderPathFinder(start);
    setupPathFinder(pathFinder);
  }

  protected void navigateToGridObject(GridObject gridObject) {
    if (gridObject == null) {
      System.out.println(String.format("%s is bored.", this.getClass().getSimpleName()));
      wanderAround();
      return;
    }
    movingTo = gridObject;

    GridPosition start = GridPositionCache.instance().getPosition(gameGrid.closestGridPoint(getX(), getY()));
    GridPosition goal = GridPositionCache.instance().getPosition(gridObject.getPosition());

    final TransitPathFinder pathFinder = new TransitPathFinder(start, goal);
    setupPathFinder(pathFinder);
  }

  private void setupPathFinder(final TransitPathFinder pathFinder) {
    pathFinder.setCompleteCallback(new Runnable() {
      public void run() {
        createSteeringManagerFromPath(pathFinder);
      }
    });

    PathSearchManager.instance().queue(pathFinder);
  }

  private void createSteeringManagerFromPath(TransitPathFinder pathFinder) {
    if (pathFinder.wasSuccessful()) {
      steeringManager = new AvatarSteeringManager(this, gameGrid, pathFinder.getDiscoveredPath());
      steeringManager.setCompleteCallback(new Runnable() {
        public void run() {
          afterReachingTarget();
        }
      });
      steeringManager.start();
    }
  }

  private void afterReachingTarget() {
    if (movingTo instanceof CommercialSpace) {
      CommercialSpace.class.cast(movingTo).recordVisitor(this);
    }

    movingTo = null;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

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
    } else {
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

  public void tap(Vector2 worldPoint, int count) {
    displaySpeechBubble("Hello!");
  }
}
