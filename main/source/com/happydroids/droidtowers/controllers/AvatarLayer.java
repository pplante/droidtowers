/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
import java.util.List;

import static com.happydroids.droidtowers.TowerConsts.AVATAR_POPULATION_SCALE;
import static com.happydroids.droidtowers.TowerConsts.MAX_AVATARS;
import static com.happydroids.droidtowers.types.ProviderType.JANITORS;
import static com.happydroids.droidtowers.types.ProviderType.MAIDS;
import static com.happydroids.droidtowers.types.ProviderType.SECURITY;

public class AvatarLayer extends GameLayer {
  private static final String TAG = AvatarLayer.class.getSimpleName();

  private final GameGrid gameGrid;
  private int specialAvatars;

  public AvatarLayer(GameGrid gameGrid) {
    super();

    this.gameGrid = gameGrid;
    setTouchEnabled(true);

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
    if (gameObjects.size() < maxAvatars()) {
      int numToSpawn = maxAvatars() - gameObjects.size();
      for (int i = 0; i <= numToSpawn; i++) {
        Avatar avatar = new Avatar(this);
        setupAvatar(avatar);
      }
    }
  }

  private int maxAvatars() {
    int totalPopulation = Player.instance().getTotalPopulation();
    if (totalPopulation > 0) {
      return (int) MathUtils.clamp(totalPopulation * AVATAR_POPULATION_SCALE, 1, MAX_AVATARS);
    }

    return 0;
  }

  private void setupAvatar(Avatar avatar) {
    boolean positionSet = false;

    if (!(avatar instanceof Janitor || avatar instanceof Maid)) {
      List<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
      if (rooms != null) {
        GridObject avatarsHome = Iterables.find(rooms, AVATAR_HOME_FILTER, null);

        if (avatarsHome != null) {
          avatar.setHome(avatarsHome);
          positionSet = true;
        }
      }
    }

    if (!positionSet) {
      avatar.setPosition(Random.randomInt(-avatar.getWidth(), gameGrid.getWorldSize().x + avatar.getWidth()), TowerConsts.GROUND_HEIGHT);
    }

    if (avatar instanceof Janitor) {
      specialAvatars++;
    }

    addChild(avatar);
  }

  public void adjustAvatarPositions(int adjustX) {
    for (GameObject avatar : gameObjects) {
      avatar.setX(avatar.getX() + gameGrid.toWorldSpace(adjustX));
      ((Avatar) avatar).cancelMovement();
    }
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    return false;
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    if (event.gridObject instanceof Room) {
      RoomType roomType = (RoomType) event.gridObject.getGridObjectType();
      if (roomType.provides(JANITORS)) {
        setupAvatar(new Janitor(this));
        setupAvatar(new Janitor(this));
        setupAvatar(new Janitor(this));
      } else if (roomType.provides(ProviderType.MAIDS)) {
        setupAvatar(new Maid(this));
        setupAvatar(new Maid(this));
        setupAvatar(new Maid(this));
      } else if (roomType.provides(ProviderType.SECURITY)) {
        setupAvatar(new SecurityGuard(this));
        setupAvatar(new SecurityGuard(this));
        setupAvatar(new SecurityGuard(this));
      }
    }
  }

  @Subscribe
  public void GameEvent_GridObjectRemoved(GridObjectRemovedEvent event) {
    GridObject gridObject = event.gridObject;

    if ((gridObject instanceof Room) && gridObject.isPlaced()) {
      RoomType roomType = (RoomType) gridObject.getGridObjectType();
      if (!roomType.provides(MAIDS, JANITORS, SECURITY)) return;

      int numDeleted = 0;
      for (GameObject avatar : gameObjects) {
        if (numDeleted > 3) break;

        if (!(avatar instanceof Maid) || !roomType.provides(MAIDS)) {
          continue;
        } else if (!(avatar instanceof Janitor) || !roomType.provides(JANITORS)) {
          continue;
        } else if (!(avatar instanceof SecurityGuard) || !roomType.provides(JANITORS)) {
          continue;
        }

        avatar.markToRemove(true);
        numDeleted++;
        specialAvatars--;
      }
    }
  }

  public static final Predicate<GridObject> AVATAR_HOME_FILTER = new Predicate<GridObject>() {
    @Override
    public boolean apply(@Nullable GridObject input) {
      if (input instanceof Room) {
        Room room = (Room) input;
        return room.isConnectedToTransport() && !room.hasResident();
      }

      return false;
    }
  };
}
