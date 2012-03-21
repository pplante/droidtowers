package com.unhappyrobot.controllers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.TowerConsts;
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
  private static final int MAX_AVATARS = (Gdx.app.getType() == Application.ApplicationType.Android ? 20 : 60);
  private Set<Avatar> avatars;
  private Set<Janitor> janitors;
  private Set<Maid> maids;
  private static final float AVATAR_POPULATION_SCALE = 0.25f;

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

    gameGrid.events().register(this);
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
    if (avatars.size() < maxAvatars()) {
      int numToSpawn = maxAvatars() - avatars.size();
      for (int i = 0; i <= numToSpawn; i++) {
        Avatar avatar = new Avatar(this);
        setupAvatar(avatar);
      }
    }
  }

  private int maxAvatars() {
    return (int) Math.min(Player.instance().getTotalPopulation() * AVATAR_POPULATION_SCALE, MAX_AVATARS);
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

  private void setupAvatar(Avatar avatar) {
    avatar.setPosition(Random.randomInt(-64, gameGrid.getWorldSize().x + 64), TowerConsts.GROUND_HEIGHT);

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
        switch (roomType.provides()) {
          case JANITORS:
            setupAvatar(new Janitor(this));
            setupAvatar(new Janitor(this));
            setupAvatar(new Janitor(this));
            break;

          case MAIDS:
            setupAvatar(new Maid(this));
            setupAvatar(new Maid(this));
            setupAvatar(new Maid(this));
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
            removeChild(Iterables.getFirst(janitors, null));
            removeChild(Iterables.getFirst(janitors, null));
            break;

          case MAIDS:
            removeChild(Iterables.getFirst(maids, null));
            removeChild(Iterables.getFirst(maids, null));
            removeChild(Iterables.getFirst(maids, null));
            break;
        }
      }
    }
  }
}
