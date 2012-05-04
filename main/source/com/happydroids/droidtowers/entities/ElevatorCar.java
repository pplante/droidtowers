/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.Passenger;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.tween.TweenSystem;

import static aurelienribon.tweenengine.TweenCallback.COMPLETE;
import static com.happydroids.droidtowers.tween.GameObjectAccessor.POSITION_Y;

public class ElevatorCar extends GameObject {
  private final Elevator elevator;
  private ElevatorQueue queue;
  private boolean inUse;

  public ElevatorCar(Elevator parent, TextureAtlas elevatorAtlas) {
    elevator = parent;
    elevator.eventBus().register(this);

    queue = new ElevatorQueue();

    TextureAtlas.AtlasRegion carRegion = elevatorAtlas.findRegion("elevator/car");
    setRegion(carRegion);
    setOrigin(0, 0);
    float gridScale = elevator.gameGrid.getGridScale();
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

  public void moveToFloor(int nextFloor) {
    System.out.println("Moving to: " + nextFloor);
    TweenSystem.getTweenManager().killTarget(this);
    GridPoint finalPosition = elevator.getContentPosition().cpy();
    finalPosition.y = nextFloor;
    float targetYPosition = finalPosition.toWorldVector2().y;
    int distanceBetweenStops = (int) (Math.abs(getY() - targetYPosition) * 5);
    Tween.to(this, POSITION_Y, distanceBetweenStops)
            .target(targetYPosition)
            .setCallback(new TweenCallback() {
              public void onEvent(int type, BaseTween source) {
                queue.informPassengers();
                inUse = false;
              }
            })
            .setCallbackTriggers(COMPLETE)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public void update(float timeDelta) {
    if (queue.waitingOnRiders()) return;

    for (Passenger passenger : queue.getCurrentRiders()) {
      passenger.updatePosition(getY());
    }

    moveToNext();
  }

  private void moveToNext() {
    if (inUse) return;

    if (!queue.moveToNextStop()) {
      setColor(Color.WHITE);
      queue.determinePickups();
    }

    if (queue.getCurrentStop() != null) {
      inUse = true;
      setColor(Color.CYAN);
      moveToFloor(queue.getCurrentStop().floorNumber);
    }
  }

  private void returnToLobby() {
    moveToFloor((int) elevator.getContentPosition().y);
  }


  public void enqueue(AvatarSteeringManager steeringManager, int boarding, int destination, Runnable disembarkCallback) {
    queue.add(new Passenger(steeringManager, boarding, destination, disembarkCallback));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ElevatorCar)) return false;

    ElevatorCar that = (ElevatorCar) o;

    if (queue != null ? !queue.equals(that.queue) : that.queue != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return queue != null ? queue.hashCode() : 0;
  }
}
