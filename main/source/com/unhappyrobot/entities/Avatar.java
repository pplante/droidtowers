package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.GameObjectAccessor;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.SpeechBubble;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.pathfinding.TransitPathFinder;
import com.unhappyrobot.utils.Random;

import java.util.Iterator;
import java.util.LinkedList;
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
  private TransitPathFinder pathFinder;
  private TransitLine currentPath;
  private UnmodifiableIterator<Vector2> currentPathIterator;
  private GridObject lastCommercialSpace;
  private TransitLine transitLine;
  private static final Set<Color> colors = Sets.newHashSet(Color.DARK_GRAY, Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);
  private static Iterator colorIterator;
  private Color myColor;
  private boolean isMovingX;
  private final SpeechBubble speechBubble;

  public Avatar(AvatarLayer avatarLayer) {
    super();
    this.gameGrid = avatarLayer.getGameGrid();

    pickColor();
    setPosition(Random.randomInt(0, gameGrid.getWorldSize().x), 256);

    TextureAtlas droidAtlas = new TextureAtlas(Gdx.files.internal("characters/droid.txt"));

    setSprite(droidAtlas.createSprite("stationary"));
    size.set(getSprite().getWidth(), getSprite().getHeight());
    animation = new Animation(FRAME_DURATION, droidAtlas.findRegions("walk"));
    animationTime = 0f;

    getSprite().setColor(myColor);

    speechBubble = new SpeechBubble();
    speechBubble.followObject(this);
    HeadsUpDisplay.getInstance().addActor(speechBubble);

    findCommercialSpace();
  }

  private void pickColor() {
    if (colorIterator == null || !colorIterator.hasNext()) {
      colorIterator = colors.iterator();
    }

    myColor = (Color) colorIterator.next();
  }

  public void findCommercialSpace() {
    gameGrid.getRenderer().removeTransitLine(transitLine);

    displaySpeechBubble("Finding a new place...");

    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      if (lastCommercialSpace != null) {
        lastCommercialSpace.setRenderColor(Color.WHITE);
      }

      List<GridObject> commercials = Lists.newArrayList(Iterables.filter(commercialSpaces, new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject gridObject) {
          return lastCommercialSpace != gridObject && gridObject instanceof CommercialSpace && ((CommercialSpace) gridObject).isConnectedToTransport();
        }
      }));
      GridObject commercialSpace = commercials.get(Random.randomInt(commercials.size()));
      commercialSpace.setRenderColor(myColor);

      pathFinder = new TransitPathFinder(commercialSpace.getPosition());
      GridPoint closestGridPoint = gameGrid.closestGridPoint(position.x, position.y);
      pathFinder.compute(GridPositionCache.instance().getPosition(closestGridPoint));

      lastCommercialSpace = commercialSpace;
    }
  }

  private void displaySpeechBubble(String newText) {
    speechBubble.setText(newText);
    speechBubble.show();
  }

  private void setupNextMovement() {
    TowerGame.getTweenManager().killTarget(this);

    if (currentPathIterator != null && currentPathIterator.hasNext()) {
      isMoving = true;
      Vector2 nextPoint = currentPathIterator.next();

      GridPoint gridPoint = gameGrid.closestGridPoint(nextPoint);
      GridPosition gridPosition = GridPositionCache.instance().getPosition(gridPoint);
      if (gridPosition.containsTransit) {
        if (gridPosition.stair != null) {
          System.out.println("Moving to stairs at: " + gridPosition);
          displaySpeechBubble("Using stairs!");
          nextPoint = gridPosition.stair.getFrontPosition().toWorldVector2(gameGrid);
        } else if (gridPosition.elevator != null) {
          System.out.println("Moving to elevator at: " + gridPosition);
          displaySpeechBubble("Using elevator!");
        }
      }

      if (nextPoint.y == position.y) {
        moveHorizontallyTo(nextPoint.x);
      } else {
        moveVerticallyTo(nextPoint.y);
      }
    } else {
      currentPath = null;
      currentPathIterator = null;

      findCommercialSpace();
    }
  }

  private void moveHorizontallyTo(float newX) {
    isFlipped = newX < position.x;
    isMovingX = true;

    int moveSpeed = (int) (Math.abs(position.x - newX) / 0.03f);
    Tween.to(this, GameObjectAccessor.POSITION, moveSpeed).target(newX, position.y).start(TowerGame.getTweenManager()).addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        isMoving = false;

        setupNextMovement();
      }
    });
  }

  private void moveVerticallyTo(float newY) {
    isMovingX = false;

    int moveSpeed = (int) (Math.abs(position.y - newY) / 0.03f);
    Tween.to(this, GameObjectAccessor.POSITION, moveSpeed).target(position.x, newY).start(TowerGame.getTweenManager()).addCallback(TweenCallback.EventType.COMPLETE, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        isMoving = false;

        setupNextMovement();
      }
    });
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    animationTime += timeDelta;

    if (isMoving && isMovingX) {
      TextureRegion keyFrame = animation.getKeyFrame(animationTime, true);
      getSprite().setRegion(keyFrame);
    }

    getSprite().flip(isFlipped, false);

    if (animationTime >= FRAME_DURATION * 3) {
      animationTime = 0f;
    }

    if (pathFinder != null) {
      if (!pathFinder.isWorking()) {
        Double pathFinderCost = pathFinder.getCost();
        LinkedList<GridPosition> discoveredPath = pathFinder.getDiscoveredPath();

        pathFinder = null;

        if (pathFinderCost == Double.MAX_VALUE) {
          findCommercialSpace();
        } else if (discoveredPath != null) {
          transitLine = new TransitLine(gameGrid);
          transitLine.setColor(myColor);
          for (GridPosition gridPosition : discoveredPath) {
            GridPoint gridPoint = new GridPoint(gridPosition.x, gridPosition.y);
            transitLine.addPoint(gridPoint.toWorldVector2(gameGrid));
          }

          gameGrid.getRenderer().addTransitLine(transitLine);

          currentPath = transitLine;
          currentPathIterator = currentPath.getPoints().iterator();

          setupNextMovement();
        }
      } else {
        for (int i = 0; i < 50; i++) {
          pathFinder.step();
        }
      }
    }
  }

  public Sprite getSprite() {
    return sprite;
  }
}
