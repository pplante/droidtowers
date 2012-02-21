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
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.AvatarState;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.SpeechBubble;
import com.unhappyrobot.pathfinding.TransitPathFinder;
import com.unhappyrobot.utils.Random;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static com.unhappyrobot.math.Direction.LEFT;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  public static final float WALKING_ANIMATION_DURATION = FRAME_DURATION * 3;
  private static final Set<Color> colors = Sets.newHashSet(Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);
  private static Iterator colorIterator;

  private final Animation walkAnimation;
  private float walkAnimationTime;

  private TransitPathFinder pathFinder;
  private AvatarSteeringManager steeringManager;

  private final GameGrid gameGrid;
  private boolean isEmployed;
  private boolean isResident;
  private float satisfactionShops;
  private float satisfactionFood;
  private Color myColor;
  private final SpeechBubble speechBubble;
  private float lastPathFinderSearch;
  private static final float PATH_SEARCH_DELAY = 25f;
  private CommercialSpace currentCommercialSpace;

  public Avatar(AvatarLayer avatarLayer) {
    super();

    gameGrid = avatarLayer.getGameGrid();

    setPosition(-Random.randomInt(10, 200), 256);

    pickColor();

    TextureAtlas droidAtlas = getTextureAtlas();
    TextureAtlas.AtlasRegion stationary = droidAtlas.findRegion("stationary");
    setSize(stationary.originalWidth, stationary.originalHeight);
    setRegion(stationary);

    walkAnimation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    walkAnimationTime = 0f;

    speechBubble = new SpeechBubble();
    speechBubble.followObject(this);
    HeadsUpDisplay.getInstance().addActor(speechBubble);
  }

  protected TextureAtlas getTextureAtlas() {
    return new TextureAtlas(Gdx.files.internal("characters/droid.txt"));
  }

  private void displaySpeechBubble(String newText) {
    speechBubble.setText(newText);
    speechBubble.show();
  }

  public void findCommercialSpace() {
    GuavaSet<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      commercialSpaces.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).isConnectedToTransport();
        }
      });

      if (commercialSpaces.size() > 0) {
        GridObject commercialSpace = commercialSpaces.getRandomEntry();

        currentCommercialSpace = (CommercialSpace) commercialSpace;
        pathFinder = new TransitPathFinder(commercialSpace.getPosition());
        pathFinder.compute(GridPositionCache.instance().getPosition(gameGrid.closestGridPoint(getX(), getY())));
      }
    }
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    if (pathFinder == null && steeringManager == null) {
      lastPathFinderSearch += timeDelta;
      if (lastPathFinderSearch >= PATH_SEARCH_DELAY) {
        lastPathFinderSearch = 0f;

        findCommercialSpace();
      }
      return;
    }

    if (pathFinder != null) {
      if (pathFinder.isWorking()) {
        for (int i = 0; i < 50; i++) {
          pathFinder.step();
        }
      } else {
        Double pathFinderCost = pathFinder.getCost();
        LinkedList<GridPosition> discoveredPath = pathFinder.getDiscoveredPath();

        pathFinder = null;

        if (discoveredPath != null) {
          steeringManager = new AvatarSteeringManager(this, gameGrid, discoveredPath);
          steeringManager.start();
          steeringManager.setCompleteCallback(new Runnable() {
            public void run() {
              currentCommercialSpace.recordVisitor(Avatar.this);
            }
          });
        }
      }
    }

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
