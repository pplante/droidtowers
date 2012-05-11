/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.Gdx;
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

import static com.happydroids.droidtowers.types.ProviderType.JANITORS;
import static com.happydroids.droidtowers.types.ProviderType.MAIDS;

public class AvatarLayer extends GameLayer {
  private static final String TAG = AvatarLayer.class.getSimpleName();

  private final GameGrid gameGrid;

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
    return (int) Math.min(Player.instance().getTotalPopulation() * TowerConsts.AVATAR_POPULATION_SCALE, TowerConsts.MAX_AVATARS);
  }

  private void setupAvatar(Avatar avatar) {
    boolean positionSet = false;

    if (!(avatar instanceof Janitor || avatar instanceof Maid)) {
      List<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
      if (rooms != null) {
        GridObject avatarsHome = Iterables.find(rooms, AVATAR_HOME_FILTER, null);

        if (avatarsHome != null) {
          Gdx.app.log(TAG, "Moving into " + avatarsHome);
          avatar.setHome(avatarsHome);
          positionSet = true;
        }
      }
    }

    if (!positionSet) {
      avatar.setPosition(Random.randomInt(-avatar.getWidth(), gameGrid.getWorldSize().x + avatar.getWidth()), TowerConsts.GROUND_HEIGHT);
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
      }
    }
  }

  @Subscribe
  public void GameEvent_GridObjectRemoved(GridObjectRemovedEvent event) {
    GridObject gridObject = event.gridObject;

    if ((gridObject instanceof Room) && gridObject.isPlaced()) {
      RoomType roomType = (RoomType) gridObject.getGridObjectType();
      if (!roomType.provides(MAIDS, JANITORS)) return;

      int numDeleted = 0;
      for (GameObject gameObject : gameObjects) {
        if (numDeleted > 3) break;

        if (gameObject instanceof Maid && roomType.provides(MAIDS) || gameObject instanceof Janitor && roomType.provides(JANITORS)) {
          gameObject.markToRemove(true);
          numDeleted++;
        }
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
