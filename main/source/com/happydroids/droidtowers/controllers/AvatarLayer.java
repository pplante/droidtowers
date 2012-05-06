/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import com.happydroids.droidtowers.types.RoomType;
import com.happydroids.droidtowers.utils.Random;

import javax.annotation.Nullable;
import java.util.Set;

import static com.happydroids.droidtowers.entities.GridObjectPlacementState.PLACED;

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

    if (gameObject instanceof Maid) {
      maids.add((Maid) gameObject);
    } else if (gameObject instanceof Janitor) {
      janitors.add((Janitor) gameObject);
    } else {
      avatars.add((Avatar) gameObject);
    }
  }


  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public void removeChild(GameObject gameObject) {
    super.removeChild(gameObject);

    janitors.remove(gameObject);
    maids.remove(gameObject);
    avatars.remove(gameObject);
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    if (event.gridObject instanceof Room) {
      RoomType roomType = (RoomType) event.gridObject.getGridObjectType();
      if (roomType.provides(ProviderType.JANITORS)) {
        setupAvatar(new Janitor(this));
        setupAvatar(new Janitor(this));
        setupAvatar(new Janitor(this));
      } else if (roomType.provides(ProviderType.MAIDS)) {
        setupAvatar(new Maid(this));
        setupAvatar(new Maid(this));
        setupAvatar(new Maid(this));
      }
    }
  }

  @Subscribe
  public void GameEvent_GridObjectRemoved(GridObjectRemovedEvent event) {
    GridObject gridObject = event.gridObject;

    if ((gridObject instanceof Room) && gridObject.getPlacementState().equals(PLACED)) {
      RoomType roomType = (RoomType) gridObject.getGridObjectType();
      if (roomType.provides(ProviderType.JANITORS)) {
        removeChild(Iterables.getFirst(janitors, null));
        removeChild(Iterables.getFirst(janitors, null));
        removeChild(Iterables.getFirst(janitors, null));
      } else if (roomType.provides(ProviderType.MAIDS)) {
        removeChild(Iterables.getFirst(maids, null));
        removeChild(Iterables.getFirst(maids, null));
        removeChild(Iterables.getFirst(maids, null));
      }
    }
  }

  public void adjustAvatarPositions(int adjustX) {
    for (Avatar avatar : avatars) {
      avatar.setX(avatar.getX() + gameGrid.toWorldSpace(adjustX));
      avatar.cancelMovement();
    }
  }
}
