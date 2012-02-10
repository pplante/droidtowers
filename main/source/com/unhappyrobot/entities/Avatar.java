package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.SpeechBubble;
import com.unhappyrobot.pathfinding.TransitPathFinder;
import com.unhappyrobot.utils.Random;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Avatar extends GameObject {
  public static final float FRAME_DURATION = 0.25f;
  //  public static final float MOVEMENT_SPEED = 0.03f;
  public static final float MOVEMENT_SPEED = 0.1f;
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
  private GridObject lastCommercialSpace;
  private static final Set<Color> colors = Sets.newHashSet(Color.DARK_GRAY, Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.YELLOW);
  private static Iterator colorIterator;
  private Color myColor;
  private boolean isMovingX;
  private final SpeechBubble speechBubble;
  private GridPosition currentPosition;
  private AvatarSteeringManager steeringManager;

  public Avatar(AvatarLayer avatarLayer) {
    super();
    this.gameGrid = avatarLayer.getGameGrid();

    pickColor();
    float worldWidth = gameGrid.getWorldSize().x;
    setPosition(Random.randomInt(worldWidth * 0.25f, worldWidth * 0.75f), 256);

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
      pathFinder.compute(GridPositionCache.instance().getPosition(gameGrid.closestGridPoint(position.x, position.y)));

      lastCommercialSpace = commercialSpace;
    }
  }

  private void displaySpeechBubble(String newText) {
    speechBubble.setText(newText);
    speechBubble.show();
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
      if (pathFinder.isWorking()) {
        for (int i = 0; i < 50; i++) {
          pathFinder.step();
        }
      } else {
        Double pathFinderCost = pathFinder.getCost();
        LinkedList<GridPosition> discoveredPath = pathFinder.getDiscoveredPath();

        pathFinder = null;

        if (pathFinderCost == Double.MAX_VALUE) {
          findCommercialSpace();
        } else if (discoveredPath != null) {
          steeringManager = new AvatarSteeringManager(this, gameGrid, discoveredPath);
          steeringManager.start();
        }
      }
    }

    if (steeringManager != null && !steeringManager.isRunning()) {
      steeringManager = null;
      findCommercialSpace();
    }
  }

  public Sprite getSprite() {
    return sprite;
  }

  public Color getColor() {
    return myColor;
  }
}
