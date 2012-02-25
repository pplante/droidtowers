package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.events.GridObjectBoundsChangeEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.tween.GameObjectAccessor;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.Random;

import java.util.LinkedList;

import static aurelienribon.tweenengine.TweenCallback.EventType.COMPLETE;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private LinkedList<PassengerEntry> passengers;
  private boolean isInUse;
  private PassengerEntry currentPassenger;
  private final GameGrid gameGrid;

  public ElevatorCar(Elevator parent, TextureAtlas elevatorAtlas) {
    elevator = parent;
    elevator.eventBus().register(this);
    gameGrid = elevator.gameGrid;
    passengers = Lists.newLinkedList();

    TextureAtlas.AtlasRegion carRegion = elevatorAtlas.findRegion("elevator/car");
    setRegion(carRegion);
    setSize(carRegion.originalWidth, carRegion.originalHeight);
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
    Vector2 elevatorPos = elevator.getContentPosition().toWorldVector2(elevator.gameGrid);
    setPosition(elevatorPos.x, elevatorPos.y);
  }

  public void moveToFloor(GridPosition nextFloor, TweenCallback tweenCallback) {
    TweenSystem.getTweenManager().killTarget(this);

    Vector2 finalPosition = nextFloor.toWorldVector2(elevator.gameGrid);

    int distanceBetweenStops = (int) (Math.abs(getY() - finalPosition.y) * 5);

    Tween.to(this, GameObjectAccessor.POSITION_Y, distanceBetweenStops)
            .delay(500)
            .target(finalPosition.y)
            .addCallback(COMPLETE, tweenCallback)
            .start(TweenSystem.getTweenManager());
  }

  @Override
  public void update(float timeDelta) {
    if (currentPassenger == null && passengers.size() > 0) {
      currentPassenger = passengers.poll();
    }

    if (currentPassenger != null) {
      if (currentPassenger.hasBoarded) {
        currentPassenger.steeringManager.getAvatar().setPosition(getX() + currentPassenger.offsetX, getY());
      } else if (!currentPassenger.isQueued && !isInUse) {
        currentPassenger.isQueued = true;
        beginCarMovement();
      }
    }
  }

  private void beginCarMovement() {
    final AvatarSteeringManager steeringManager = currentPassenger.steeringManager;
    moveToFloor(currentPassenger.boardingFloor, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        // add a bit of padding to move avatar into middle of car.

        Vector2 avatarWalkToElevator = steeringManager.getCurrentPosition().toWorldVector2(gameGrid);
        avatarWalkToElevator.x += currentPassenger.offsetX;

        steeringManager.moveAvatarTo(avatarWalkToElevator, new TweenCallback() {
          public void onEvent(EventType eventType, BaseTween source) {
            currentPassenger.hasBoarded = true;

            moveToFloor(currentPassenger.destinationFloor, new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                steeringManager.moveAvatarTo(steeringManager.getNextPosition().toWorldVector2(gameGrid), new TweenCallback() {
                  public void onEvent(EventType eventType, BaseTween source) {
                    currentPassenger.runCompleteCallback();
                    currentPassenger = null;
                  }
                });
              }
            });
          }
        });
      }
    });
  }

  public PassengerEntry addPassenger(AvatarSteeringManager steeringManager) {
    final PassengerEntry entry = new PassengerEntry(steeringManager, steeringManager.getCurrentPosition(), steeringManager.getNextPosition());
    passengers.add(entry);

    return entry;
  }

  public class PassengerEntry {
    private final AvatarSteeringManager steeringManager;
    private final GridPosition boardingFloor;
    private final GridPosition destinationFloor;
    private Runnable runnable;
    public boolean hasBoarded;
    public boolean isQueued;
    public float offsetX;

    public PassengerEntry(AvatarSteeringManager steeringManager, GridPosition boardingFloor, GridPosition destinationFloor) {
      this.steeringManager = steeringManager;
      this.boardingFloor = boardingFloor;
      this.destinationFloor = destinationFloor;

      offsetX = Random.randomInt(8, 36);
    }

    public void addCallback(Runnable runnable) {
      this.runnable = runnable;
    }

    public void runCompleteCallback() {
      runnable.run();
    }
  }
}
