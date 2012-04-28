/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.ElevatorPassengerEntry;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;

import java.util.LinkedList;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private LinkedList<ElevatorPassengerEntry> elevatorPassengers;
  private boolean isInUse;
  private ElevatorPassengerEntry currentElevatorPassenger;
  private final GameGrid gameGrid;

  public ElevatorCar(Elevator parent, TextureAtlas elevatorAtlas) {
    elevator = parent;
    elevator.eventBus().register(this);
    gameGrid = elevator.gameGrid;
    elevatorPassengers = Lists.newLinkedList();

    TextureAtlas.AtlasRegion carRegion = elevatorAtlas.findRegion("elevator/car");
    setRegion(carRegion);
    setOrigin(0, 0);
    float gridScale = gameGrid.getGridScale();
    setSize(carRegion.originalWidth * gridScale, carRegion.originalHeight * gridScale);
    setScale(gridScale);
  }

  @Override
  protected void finalize() throws Throwable {
    if (elevator != null) {
      elevator.eventBus().unregister(this);
    }

    super.finalize();
  }

  @Subscribe
  public void Elevator_boundsChanged(GridObjectBoundsChangeEvent event) {
    setPosition(elevator.getWorldPosition());
  }

  public void moveToFloor(GridPosition nextFloor, TweenCallback tweenCallback) {
    TweenSystem.getTweenManager().killTarget(this);

    Vector2 finalPosition = nextFloor.toWorldVector2(elevator.gameGrid);

    int distanceBetweenStops = (int) (Math.abs(getY() - finalPosition.y) * 5);

    Tween.to(this, GameObjectAccessor.POSITION_Y, distanceBetweenStops)
            .delay(500)
            .target(finalPosition.y)
            .setCallback(tweenCallback)
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public void update(float timeDelta) {
    if (currentElevatorPassenger == null && elevatorPassengers.size() > 0) {
      currentElevatorPassenger = elevatorPassengers.poll();
    }

    if (currentElevatorPassenger != null) {
      if (currentElevatorPassenger.hasBoarded) {
        currentElevatorPassenger.getSteeringManager().getAvatar().setPosition(getX() + currentElevatorPassenger.offsetX, getY());
      } else if (!currentElevatorPassenger.isQueued && !isInUse) {
        currentElevatorPassenger.isQueued = true;
        beginCarMovement();
      }
    }
  }

  private void beginCarMovement() {
    final AvatarSteeringManager steeringManager = currentElevatorPassenger.getSteeringManager();
    moveToFloor(currentElevatorPassenger.getBoardingFloor(), new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        // push a bit of padding to move avatar into middle of car.

        Vector2 avatarWalkToElevator = steeringManager.getCurrentPosition().toWorldVector2(gameGrid);
        avatarWalkToElevator.x += currentElevatorPassenger.offsetX;

        steeringManager.moveAvatarTo(avatarWalkToElevator, new TweenCallback() {
          public void onEvent(int type, BaseTween source) {
            currentElevatorPassenger.hasBoarded = true;

            moveToFloor(currentElevatorPassenger.getDestinationFloor(), new TweenCallback() {
              public void onEvent(int type, BaseTween source) {
                steeringManager.moveAvatarTo(steeringManager.getNextPosition().toWorldVector2(gameGrid), new TweenCallback() {
                  public void onEvent(int type, BaseTween source) {
                    currentElevatorPassenger.runCompleteCallback();
                    currentElevatorPassenger = null;
                  }
                });
              }
            });
          }
        });
      }
    });
  }

  public ElevatorPassengerEntry addPassenger(AvatarSteeringManager steeringManager) {
    final ElevatorPassengerEntry entryElevator = new ElevatorPassengerEntry(steeringManager, steeringManager.getCurrentPosition(), steeringManager.getNextPosition());
    elevatorPassengers.add(entryElevator);

    return entryElevator;
  }

}
