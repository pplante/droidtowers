/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.events.EmployeeHiredEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import com.happydroids.droidtowers.types.RoomType;
import com.happydroids.droidtowers.utils.Random;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;

import static com.happydroids.droidtowers.types.ProviderType.*;

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
    return Player.instance().getMaxPopulation() + Player.instance().getJobsFilled();
  }

  private void setupAvatar(Avatar avatar) {
    boolean positionSet = false;

    List<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    if (rooms != null) {
      List<GridObject> roomsSorted = Ordering.natural().reverse().onResultOf(new Function<GridObject, Comparable>() {
        @Override
        public Comparable apply(@Nullable GridObject input) {
          return input.getDesirability();
        }
      }).sortedCopy(rooms);
      GridObject avatarsHome = Iterables.find(roomsSorted, AVATAR_HOME_FILTER, null);

      if (avatarsHome != null) {
        avatar.setHome(avatarsHome);
        positionSet = true;
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

  private void setupSpecialAvatar(CommercialSpace commercialSpace, Class<? extends Avatar> avatarClass) {
    for (JobCandidate employee : commercialSpace.getEmployees()) {
      try {
        Constructor<? extends Avatar> constructor = avatarClass.getDeclaredConstructor(AvatarLayer.class);
        Avatar avatar = constructor.newInstance(this);
        setupAvatar(avatar);
        employee.setAvatar(avatar);
      } catch (Exception e) {
        throw new RuntimeException(e);
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

  @Subscribe
  public void GameGrid_onEmployeeHired(EmployeeHiredEvent event) {
    if (event.gridObject instanceof CommercialSpace) {
      CommercialSpace commercialSpace = (CommercialSpace) event.gridObject;
      if (commercialSpace.provides(JANITORS)) {
        setupSpecialAvatar(commercialSpace, Janitor.class);
      } else if (commercialSpace.provides(ProviderType.MAIDS)) {
        setupSpecialAvatar(commercialSpace, Maid.class);
      } else if (commercialSpace.provides(ProviderType.SECURITY)) {
        setupSpecialAvatar(commercialSpace, SecurityGuard.class);
      }
    }
  }

  public static final Predicate<GridObject> AVATAR_HOME_FILTER = new Predicate<GridObject>() {
    @Override
    public boolean apply(@Nullable GridObject input) {
      if (input instanceof Room) {
        Room room = (Room) input;
        return room.isConnectedToTransport() && (room.getNumResidents() == 0 || room.getNumResidents() < room.getNumSupportedResidents());
      }

      return false;
    }
  };
}
