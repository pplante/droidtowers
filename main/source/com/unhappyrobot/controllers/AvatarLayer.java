package com.unhappyrobot.controllers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.entities.*;

public class AvatarLayer extends GameLayer {
  private static AvatarLayer instance;
  private final GameGrid gameGrid;
  private static final int MAX_AVATARS = 20;

  public static void initialize(GameGrid gameGrid) {
    instance = new AvatarLayer(gameGrid);
  }

  public static AvatarLayer instance() {
    return instance;
  }

  AvatarLayer(GameGrid gameGrid) {
    super();

    this.gameGrid = gameGrid;
    setTouchEnabled(true);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    if (gameObjects.size() < MAX_AVATARS) {
      System.out.println("gameObjects = " + gameObjects.size());
      GuavaSet<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
      if (rooms != null) {
        rooms.filterBy(new Predicate<GridObject>() {
          public boolean apply(@Nullable GridObject gridObject) {
            return ((Room) gridObject).isConnectedToTransport();
          }
        });
      }

      int numToSpawn = MAX_AVATARS - gameObjects.size();
      for (int i = 0; i <= numToSpawn; i++) {
        Avatar avatar = new Avatar(this);

        setupAvatar(rooms, avatar);
      }

      Janitor janitor = new Janitor(this);
      setupAvatar(rooms, janitor);

      Maid maid = new Maid(this);
      setupAvatar(rooms, maid);
    }

  }

  private void setupAvatar(GuavaSet<GridObject> rooms, Avatar avatar) {
    if (rooms != null) {
      avatar.setPosition(rooms.getRandomEntry().getContentPosition().toWorldVector2(gameGrid));
    }

    avatar.beginNextAction();

    addChild(avatar);
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
