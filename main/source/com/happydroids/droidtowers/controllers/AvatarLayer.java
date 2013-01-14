/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.ProviderType;
import com.happydroids.droidtowers.utils.Random;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import static com.happydroids.droidtowers.types.ProviderType.JANITORS;

public class AvatarLayer extends GameLayer<Avatar> {
  private static final String TAG = AvatarLayer.class.getSimpleName();

  private final GameGrid gameGrid;
  private float timeUntilAvatarMaintenance;


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

    timeUntilAvatarMaintenance -= timeDelta;
    if (timeUntilAvatarMaintenance <= 0) {
      timeUntilAvatarMaintenance = TowerConsts.AVATAR_SPAWN_DELAY;
      maintainAvatars();
    }
  }

  @Override
  public void render(SpriteBatch spriteBatch, OrthographicCamera camera) {
    if (!isVisible()) {
      return;
    }

    spriteBatch.begin();
    spriteBatch.enableBlending();
    for (Avatar gameObject : gameObjects) {
      tmp.set(gameObject.getX(), gameObject.getY(), 0);
      if (camera.frustum.sphereInFrustum(tmp, Math.max(gameObject.getWidth(), gameObject.getHeight()))) {
        gameObject.draw(spriteBatch);
      }
    }

    spriteBatch.end();
  }

  private void maintainAvatars() {
    if (shouldSpawnMoreAvatars()) {
      int numToSpawn = maxAvatars() - gameObjects.size;
      for (int i = 0; i <= numToSpawn; i++) {
        Avatar avatar = new Avatar(this.getGameGrid());
        setupAvatar(avatar);
      }
    } else if (gameObjects.size - 1 > maxAvatars()) {
      int numToKill = gameObjects.size - maxAvatars();
      for (int i = 0; i <= numToKill; i++) {
        if (i < gameObjects.size) {
          GameObject gameObject = gameObjects.get(i);
          if (!(gameObject instanceof Janitor)) {
            gameObject.markToRemove(true);
          }
        }
      }
    }
  }

  private boolean shouldSpawnMoreAvatars() {
    return gameObjects.size < maxAvatars();
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

  private void setupSpecialAvatar(CommercialSpace commercialSpace, Class<? extends Avatar> avatarClass, int numToCreate) {
    try {
      Constructor<? extends Avatar> constructor = avatarClass.getDeclaredConstructor(AvatarLayer.class);
      for (int i = 0; i < numToCreate; i++) {
        Avatar avatar = constructor.newInstance(this);
        setupAvatar(avatar);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Subscribe
  public void GameEvent_GridObjectPlaced(GridObjectPlacedEvent event) {
    if (event.getGridObject() instanceof ServiceRoom) {
      ServiceRoom commercialSpace = (ServiceRoom) event.getGridObject();
      if (commercialSpace.provides(JANITORS)) {
        setupSpecialAvatar(commercialSpace, Janitor.class, 3);
      } else if (commercialSpace.provides(ProviderType.MAIDS)) {
        setupSpecialAvatar(commercialSpace, Maid.class, 2);
      } else if (commercialSpace.provides(ProviderType.SECURITY)) {
        setupSpecialAvatar(commercialSpace, SecurityGuard.class, 2);
      }
    }
  }

  @Subscribe
  public void GameEvent_GridObjectRemoved(GridObjectRemovedEvent event) {
    GridObject gridObject = event.getGridObject();

    if ((gridObject instanceof Room) && gridObject.isPlaced()) {
      Room room = (Room) event.getGridObject();
      for (Avatar avatar : room.getResidents()) {
        avatar.markToRemove(true);
      }
    }
  }

  public int getNumAvatars() {
    return gameObjects.size;
  }

  public void setupInitialAvatars() {
    maintainAvatars();

    Array<GridObject> rooms = new Array<GridObject>(gameGrid.getInstancesOf(Room.class).items);
    Iterables.removeIf(rooms, new Predicate<GridObject>() {
      @Override
      public boolean apply(@Nullable GridObject input) {
        return input == null;
      }
    });

    if (rooms != null && rooms.size > 0) {
      rooms.sort(GridObjectSort.byDesirability);
      Iterator<GridObject> iterator = rooms.iterator();

      for (Avatar avatar : gameObjects) {
        if (iterator.hasNext()) {
          Room newHome = (Room) iterator.next();
          avatar.setHome(newHome);
          if (newHome.getNumResidents() >= newHome.getNumSupportedResidents()) {
            iterator.remove();
          }
        } else {
          break;
        }
      }
    }
  }
}
