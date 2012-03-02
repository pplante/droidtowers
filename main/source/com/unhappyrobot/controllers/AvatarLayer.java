package com.unhappyrobot.controllers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.events.GridObjectRemovedEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.RoomType;
import com.unhappyrobot.utils.Random;

import java.util.Set;

public class AvatarLayer extends GameLayer {
  private static AvatarLayer instance;
  private final GameGrid gameGrid;
  private static final int MAX_AVATARS = 20;
  private Set<Avatar> avatars;
  private Set<Janitor> janitors;
  private Set<Maid> maids;

  public static AvatarLayer initialize(GameGrid gameGrid) {
    instance = new AvatarLayer(gameGrid);

    return instance;
  }

  public static AvatarLayer instance() {
    return instance;
  }

  AvatarLayer(GameGrid gameGrid) {
    super();

    this.gameGrid = gameGrid;
    setTouchEnabled(true);

    avatars = Sets.newHashSet();
    janitors = Sets.newHashSet();
    maids = Sets.newHashSet();

    GameGrid.events().register(this);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    maintainAvatars();
  }

  private void maintainAvatars() {
    if (avatars.size() < MAX_AVATARS) {
      GuavaSet<GridObject> rooms = getAllRooms();
      if (rooms == null || rooms.isEmpty()) {
        return;
      }

      int numToSpawn = MAX_AVATARS - avatars.size();
      for (int i = 0; i <= numToSpawn; i++) {
        Avatar avatar = new Avatar(this);
        setupAvatar(rooms, avatar);
      }
    }
  }

  private GuavaSet<GridObject> getAllRooms() {
    GuavaSet<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    if (rooms != null) {
      rooms.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject gridObject) {
          return ((Room) gridObject).isConnectedToTransport();
        }
      });
    }
    return rooms;
  }

  private void setupAvatar(GuavaSet<GridObject> rooms, Avatar avatar) {
    if (rooms != null) {
      avatar.setPosition(Random.randomInt(-64, gameGrid.getWorldSize().x + 64), 256f);
    }

    avatar.beginNextAction();
    addChild(avatar);
  }

  @Override
  public void addChild(GameObject gameObject) {
    super.addChild(gameObject);

    if (gameObject instanceof Janitor) {
      janitors.add((Janitor) gameObject);
    } else if (gameObject instanceof Maid) {
      maids.add((Maid) gameObject);
    } else {
      avatars.add((Avatar) gameObject);
    }
  }


  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public void removeChild(GameObject gameObject) {
    super.removeChild(gameObject);

    if (gameObject instanceof Janitor) {
      janitors.remove(gameObject);
    } else if (gameObject instanceof Maid) {
      maids.remove(gameObject);
    } else {
      avatars.remove(gameObject);
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

  @Subscribe
  public void GameEvent_GridObjectAdded(GridObjectAddedEvent event) {
    if (event.gridObject instanceof Room) {
      RoomType roomType = (RoomType) event.gridObject.getGridObjectType();
      if (roomType.provides() != null) {
        GuavaSet<GridObject> rooms = getAllRooms();

        switch (roomType.provides()) {
          case JANITORS:
            setupAvatar(rooms, new Janitor(this));
            setupAvatar(rooms, new Janitor(this));
            setupAvatar(rooms, new Janitor(this));
            break;

          case MAIDS:
            setupAvatar(rooms, new Maid(this));
            setupAvatar(rooms, new Maid(this));
            setupAvatar(rooms, new Maid(this));
            break;
        }
      }
    }
  }

  @Subscribe
  public void GameEvent_GridObjectRemoved(GridObjectRemovedEvent event) {
    if (event.gridObject instanceof Room) {
      RoomType roomType = (RoomType) event.gridObject.getGridObjectType();
      if (roomType.provides() != null) {
        switch (roomType.provides()) {
          case JANITORS:
            removeChild(Iterables.getFirst(janitors, null));
            break;

          case MAIDS:
            removeChild(Iterables.getFirst(maids, null));
            break;
        }
      }
    }
  }
}
