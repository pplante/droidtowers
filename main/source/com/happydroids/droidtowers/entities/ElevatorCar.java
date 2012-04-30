/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.ElevatorPassengerEntry;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.Direction;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.happydroids.droidtowers.math.Direction.UP;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private LinkedList<ElevatorPassengerEntry> passengerQueue;
  private boolean isInUse;
  private List<ElevatorPassengerEntry> currentPassengers;
  private final GameGrid gameGrid;
  private GridPosition currentFloor;
  private Direction currentDirection;
  private int destinationFloor;

  public ElevatorCar(Elevator parent, TextureAtlas elevatorAtlas) {
    elevator = parent;
    elevator.eventBus().register(this);
    gameGrid = elevator.gameGrid;
    passengerQueue = Lists.newLinkedList();

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
    if (currentPassengers != null) {
      for (ElevatorPassengerEntry passenger : currentPassengers) {
        passenger.getSteeringManager().getAvatar().murderDeathKill187();
      }

    }
    setPosition(elevator.getWorldPosition());
  }

  public void moveToFloor(GridPosition nextFloor) {
    TweenSystem.getTweenManager().killTarget(this);
    currentFloor = nextFloor;
    isInUse = true;
    Vector2 finalPosition = nextFloor.toWorldVector2();
    int distanceBetweenStops = (int) (Math.abs(getY() - finalPosition.y) * 5);
    Tween.to(this, GameObjectAccessor.POSITION_Y, distanceBetweenStops)
            .delay(500)
            .target(finalPosition.y)
            .setCallback(new TweenCallback() {
              public void onEvent(int type, BaseTween source) {
                isInUse = false;
              }
            })
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public void update(float timeDelta) {
    if (isInUse) {
      if (currentPassengers != null) {
        for (ElevatorPassengerEntry passenger : currentPassengers) {
          Avatar avatar = passenger.getSteeringManager().getAvatar();
          avatar.setPosition(getX() + passenger.getOffsetX(), getY());
        }
      }
      return;
    }

    if (currentPassengers == null && passengerQueue.size() > 0) {
      ElevatorPassengerEntry firstPassenger = passengerQueue.poll();
      int boardingFloor = firstPassenger.getBoardingFloor().y;
      destinationFloor = firstPassenger.getDestinationFloor().y;
      currentDirection = boardingFloor < destinationFloor ? UP : Direction.DOWN;

      List<ElevatorPassengerEntry> unsortedPassengers = Lists.newArrayList(firstPassenger);
      for (ElevatorPassengerEntry passenger : passengerQueue) {
        if (!passenger.isQueued() && passenger.getBoardingFloor().y >= boardingFloor && passenger.getDestinationFloor().y <= destinationFloor) {
          passenger.isQueued = true;
          unsortedPassengers.add(passenger);
        }

        if (unsortedPassengers.size() > 6) {
          break;
        }
      }

      currentPassengers = Ordering.natural().onResultOf(new Function<ElevatorPassengerEntry, Comparable>() {
        public Comparable apply(@Nullable ElevatorPassengerEntry input) {
          return input.getBoardingFloor().y;
        }
      }).sortedCopy(unsortedPassengers);

      currentFloor = currentPassengers.get(0).getBoardingFloor();
    }

    if (currentPassengers != null && !currentPassengers.isEmpty()) {
      Iterator<ElevatorPassengerEntry> passengerIterator = currentPassengers.iterator();
      boolean shouldWait = false;
      while (passengerIterator.hasNext()) {
        ElevatorPassengerEntry passenger = passengerIterator.next();
        if (!passenger.shouldWaitFor()) {
          if (passenger.getBoardingFloor().y == currentFloor.y) {
            passenger.boardElevator(gameGrid);
          } else if (passenger.getDestinationFloor().y == currentFloor.y) {
            passenger.disembarkFromElevator(gameGrid);
            passengerIterator.remove();
          }
        } else {
          shouldWait = true;
        }
      }

      if (!shouldWait) {
        figureOutNextFloor();

        if (currentFloor != null) {
          moveToFloor(currentFloor);
        }
      }
    } else {
      resetAndGetReadyForNextPassenger();
    }
  }

  private void resetAndGetReadyForNextPassenger() {
    TweenSystem.getTweenManager().killTarget(this);
    currentPassengers = null;
    currentFloor = null;
    currentDirection = null;

    returnToLobby();
  }

  private void returnToLobby() {
    moveToFloor(gameGrid.positionCache().getPosition(elevator.getContentPosition()));
  }

  private void figureOutNextFloor() {
    if (currentFloor != null) {
      if (currentDirection.equals(UP)) {
        currentFloor = gameGrid.positionCache().getPosition(currentFloor.x, currentFloor.y + 1);
      } else {
        currentFloor = gameGrid.positionCache().getPosition(currentFloor.x, currentFloor.y - 1);
      }
    }

    if (currentFloor == null) {
      resetAndGetReadyForNextPassenger();
    }
  }

  private boolean shouldWaitForPassengers() {
    boolean waitingOnAnyone = false;
    for (ElevatorPassengerEntry currentPassenger : currentPassengers) {
      if (currentPassenger.shouldWaitFor()) {
        waitingOnAnyone = true;
        break;
      }
    }
    return waitingOnAnyone;
  }

  private void beginCarMovement() {

  }

  public ElevatorPassengerEntry addPassenger(AvatarSteeringManager steeringManager) {
    final ElevatorPassengerEntry entryElevator = new ElevatorPassengerEntry(steeringManager, steeringManager.getCurrentPosition(), steeringManager.getNextPosition());
    passengerQueue.add(entryElevator);
    System.out.println(entryElevator + " has joined the queue!");
    return entryElevator;
  }

}
