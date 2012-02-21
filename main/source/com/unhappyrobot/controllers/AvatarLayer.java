package com.unhappyrobot.controllers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.entities.*;

public class AvatarLayer extends GameLayer {
  private static AvatarLayer instance;
  private final GameGrid gameGrid;
  private static final int MAX_AVATARS = 20;
  private static final float SPAWN_RATE = 0.5f;
  private float timeSinceLastSpawn;

  public static void initialize(GameGrid gameGrid) {
    instance = new AvatarLayer(gameGrid);
  }

  public static AvatarLayer instance() {
    return instance;
  }

  AvatarLayer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    timeSinceLastSpawn = SPAWN_RATE;
    setTouchEnabled(true);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    if (gameObjects.size() < MAX_AVATARS) {
      GuavaSet<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
      if (rooms != null) {
        rooms.filterBy(new Predicate<GridObject>() {
          public boolean apply(@Nullable GridObject gridObject) {
            return ((Room) gridObject).isConnectedToTransport();
          }
        });
      }

      for (int i = 0; i < MAX_AVATARS - gameObjects.size(); i++) {
        Avatar avatar = new Avatar(this);

        if (rooms != null) {
          avatar.setPosition(rooms.getRandomEntry().getContentPosition().toWorldVector2(gameGrid));
        }

        avatar.findCommercialSpace();

        addChild(avatar);
      }

      Janitor janitor = new Janitor(this);
      if (rooms != null) {
        janitor.setPosition(rooms.getRandomEntry().getContentPosition().toWorldVector2(gameGrid));
      }
      addChild(janitor);
    }

  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
        Avatar avatar = (Avatar) gameObject;
        avatar.tap(worldPoint, count);

        return true;
      }
    }

    return false;
  }
}
