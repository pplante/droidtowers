/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.Passenger;
import com.happydroids.droidtowers.events.ElevatorHeightChangeEvent;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.tween.TweenSystem;

import static aurelienribon.tweenengine.TweenCallback.COMPLETE;
import static com.happydroids.droidtowers.tween.GameObjectAccessor.POSITION_Y;

public class ElevatorCar extends GameObject {
  private final Elevator elevator;
  private ElevatorQueue queue;
  protected boolean inUse;
  private GridPoint finalPosition;

  public ElevatorCar(Elevator parent, TextureAtlas elevatorAtlas) {
    elevator = parent;
    elevator.eventBus().register(this);
    finalPosition = new GridPoint();

    queue = new ElevatorQueue(elevator);

    TextureAtlas.AtlasRegion carRegion = elevatorAtlas.findRegion(elevator.getGridObjectType().getImageFilename());
    setRegion(carRegion);
    setOrigin(0, 0);
    float gridScale = elevator.gameGrid.getGridScale();
    setSize(carRegion.originalWidth * gridScale, carRegion.originalHeight * gridScale);
    setScale(gridScale);
    resetToBottomOfShaft();
  }

  @Override
  protected void finalize() throws Throwable {
    if (elevator != null) {
      elevator.eventBus().unregister(this);
    }

    super.finalize();
  }

  public void moveToFloor(final int nextFloor) {
    TweenSystem.manager().killTarget(this);
    finalPosition.set(elevator.getPosition());
    finalPosition.y = nextFloor;
    float targetYPosition = finalPosition.getWorldY();
    int distanceBetweenStops = (int) (Math.abs(getY() - targetYPosition) * 2.5f);
    Tween.to(this, POSITION_Y, distanceBetweenStops)
            .target(targetYPosition)
            .delay(500f)
            .setCallback(new TweenCallback() {
              public void onEvent(int type, BaseTween source) {
                queue.arrivedAt(nextFloor);
                inUse = false;
              }
            })
            .setCallbackTriggers(COMPLETE)
            .start(TweenSystem.manager());
  }

  @Override
  public void update(float timeDelta) {
    if (queue.waitingOnRiders()) {
      return;
    }

    for (Passenger currentRider : queue.getCurrentRiders()) {
      if (currentRider.isRiding()) {
        currentRider.updatePosition(getY());
      }
    }

    if (!inUse) {
      if (!queue.moveToNextStop()) {
        queue.determinePickups();
      }

      if (queue.getNextFloor() != ElevatorQueue.INVALID_FLOOR) {
        inUse = true;
        moveToFloor(queue.getNextFloor());
      }
    }
  }

  private void returnToLobby() {
    moveToFloor(elevator.getPosition().y);
  }

  public boolean addPassenger(AvatarSteeringManager steeringManager, int boarding, int destination, Runnable disembarkCallback) {
    queue.add(new Passenger(steeringManager, boarding, destination, disembarkCallback));

    return true;
  }


  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    queue.removePassenger(avatarSteeringManager);
  }

  public void clearQueue() {
    queue.clear();
  }

  @Subscribe
  public void Elevator_boundsChanged(ElevatorHeightChangeEvent event) {
    resetToBottomOfShaft();
  }

  @Subscribe
  public void Elevator_boundsChanged(GridObjectPlacedEvent event) {
    resetToBottomOfShaft();
  }

  protected void resetToBottomOfShaft() {
    inUse = false;
    queue.informPassengersOfServiceChange();
    TweenSystem.manager().killTarget(this);
    Vector2 elevatorWorldPosition = elevator.getWorldPosition();
    setPosition(elevatorWorldPosition.x, elevatorWorldPosition.y + elevator.scaledGridUnit());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ElevatorCar)) {
      return false;
    }

    ElevatorCar that = (ElevatorCar) o;

    return !(queue != null ? !queue.equals(that.queue) : that.queue != null);
  }

  @Override
  public int hashCode() {
    return queue != null ? queue.hashCode() : 0;
  }

  public int getNumRiders() {
    return queue.getCurrentRiders().size;
  }

  public int getNumPassengersWaiting() {
    return queue.getPassengersWaiting().size;
  }

  public boolean isInUse() {
    return inUse;
  }
}
