/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.events.EmployeeFiredEvent;
import com.happydroids.droidtowers.events.EmployeeHiredEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import com.happydroids.droidtowers.utils.Random;

import java.lang.reflect.Constructor;

import static com.happydroids.droidtowers.types.ProviderType.JANITORS;

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
    } else if (gameObjects.size() - 1 > maxAvatars()) {
      int numToKill = gameObjects.size() - maxAvatars();
      for (int i = 0; i <= numToKill; i++) {
        if (i < gameObjects.size()) {
          GameObject gameObject = gameObjects.get(i);
          if (!(gameObject instanceof Janitor)) {
            gameObject.markToRemove(true);
          }
        }
      }
    }
  }

  private int maxAvatars() {
    return Player.instance().getSupportedResidency();
//    return 0;
  }

  private void setupAvatar(Avatar avatar) {
    boolean positionSet = false;

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
      if (employee.hasAvatar()) continue;

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
      Room room = (Room) event.gridObject;
      for (Avatar avatar : room.getResidents()) {
        avatar.markToRemove(true);
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

  @Subscribe
  public void GameGrid_onEmployeeFired(EmployeeFiredEvent event) {
    if (event.gridObject instanceof CommercialSpace && event.employee != null) {
      Avatar avatar = event.employee.getAvatar();
      if (avatar != null) {
        avatar.markToRemove(true);
      }
    }
  }

  public int getNumAvatars() {
    return gameObjects.size() - 1;
  }
}
