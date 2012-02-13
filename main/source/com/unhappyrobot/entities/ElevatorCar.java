package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.controllers.GameObjectAccessor;
import com.unhappyrobot.events.GridObjectBoundsChangeEvent;

import java.util.Map;

import static aurelienribon.tweenengine.TweenCallback.EventType.COMPLETE;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private Map<AvatarSteeringManager, PassengerEntry> passengers;

  public ElevatorCar(Elevator elevator, TextureAtlas elevatorAtlas) {
    this.elevator = elevator;
    elevator.eventBus().register(this);

    passengers = Maps.newHashMap();
    setRegion(elevatorAtlas.findRegion("elevator/car"));
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
    Vector2 finalPosition = nextFloor.toWorldVector2(elevator.gameGrid);

    float dst = finalPosition.dst(getX(), getY());

    Tween.to(this, GameObjectAccessor.POSITION, (int) (dst * 0.03f))
            .target(finalPosition.x, finalPosition.y)
            .addCallback(COMPLETE, tweenCallback)
            .start(TowerGame.getTweenManager());
  }

  @Override
  public void update(float timeDelta) {
    for (final PassengerEntry entry : passengers.values()) {
      if (entry.hasBoarded) {
        entry.steeringManager.getAvatar().setPosition(getX(), getY());
      } else if (!entry.isQueued) {
        entry.isQueued = true;
        moveToFloor(entry.boardingFloor, new TweenCallback() {
          public void onEvent(EventType eventType, BaseTween source) {
            entry.steeringManager.moveAvatarTo(entry.steeringManager.getCurrentPosition(), new TweenCallback() {
              public void onEvent(EventType eventType, BaseTween source) {
                entry.hasBoarded = true;

                moveToFloor(entry.destinationFloor, new TweenCallback() {
                  public void onEvent(EventType eventType, BaseTween source) {
                    entry.runnable.run();
                  }
                });
              }
            });
          }
        });
      }
    }
  }

  public PassengerEntry addPassenger(AvatarSteeringManager steeringManager) {
    final PassengerEntry entry = new PassengerEntry(steeringManager, steeringManager.getCurrentPosition(), steeringManager.getNextPosition());
    passengers.put(steeringManager, entry);

    return entry;
  }

  public void removePassenger(AvatarSteeringManager steeringManager) {
    passengers.remove(steeringManager);
  }

  public class PassengerEntry {
    private final AvatarSteeringManager steeringManager;
    private final GridPosition boardingFloor;
    private final GridPosition destinationFloor;
    private Runnable runnable;
    public boolean hasBoarded;
    public boolean isQueued;

    public PassengerEntry(AvatarSteeringManager steeringManager, GridPosition boardingFloor, GridPosition destinationFloor) {
      this.steeringManager = steeringManager;
      this.boardingFloor = boardingFloor;
      this.destinationFloor = destinationFloor;
    }

    public void addCallback(Runnable runnable) {
      this.runnable = runnable;
    }
  }
}
