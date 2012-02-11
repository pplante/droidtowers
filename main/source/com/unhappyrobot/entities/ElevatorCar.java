package com.unhappyrobot.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.AvatarSteeringManager;
import com.unhappyrobot.controllers.GameObjectAccessor;
import com.unhappyrobot.events.GridObjectBoundsChangeEvent;
import com.unhappyrobot.math.GridPoint;

import java.util.Map;

import static aurelienribon.tweenengine.TweenCallback.EventType.COMPLETE;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private final Sprite sprite;
  private Map<AvatarSteeringManager, PassengerEntry> passengers;

  public ElevatorCar(Elevator elevator, TextureAtlas elevatorAtlas) {
    this.elevator = elevator;
    elevator.eventBus().register(this);

    passengers = Maps.newHashMap();
    sprite = elevatorAtlas.createSprite("elevator/car");
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
    position = elevator.getContentPosition().toWorldVector2(elevator.gameGrid);
  }

  public void moveToFloor(int nextFloor, TweenCallback tweenCallback) {
    nextFloor = Math.max(0, Math.min((int) elevator.getContentSize().y, nextFloor));
    int numFloors = Math.abs(nextFloor - floor);
    System.out.println("numFloors = " + numFloors);
    if (numFloors > 0) {
      GridPoint gridPoint = elevator.getContentPosition().cpy();
      gridPoint.y += nextFloor;

      Vector2 finalPosition = gridPoint.toWorldVector2(elevator.gameGrid);

      Tween.to(this, GameObjectAccessor.POSITION, 300 * numFloors)
              .target(finalPosition.x, finalPosition.y)
              .addCallback(COMPLETE, tweenCallback)
              .start(TowerGame.getTweenManager());

      floor = nextFloor;
    } else {
      tweenCallback.onEvent(null, null);
    }
  }

  @Override
  public void update(float timeDelta) {
    for (final PassengerEntry entry : passengers.values()) {
      if (entry.hasBoarded) {
        entry.steeringManager.getAvatar().setPosition(position);
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

  public void draw(SpriteBatch spriteBatch) {
    sprite.setPosition(position.x, position.y);
    sprite.draw(spriteBatch);
  }

  public PassengerEntry addPassenger(AvatarSteeringManager steeringManager) {
    int boardingFloor = steeringManager.getCurrentPosition().y - 4;
    final int destinationFloor = steeringManager.getNextPosition().y - 4;

    final PassengerEntry entry = new PassengerEntry(steeringManager, boardingFloor, destinationFloor);
    passengers.put(steeringManager, entry);

    return entry;
  }

  public void removePassenger(AvatarSteeringManager steeringManager) {
    passengers.remove(steeringManager);
  }

  public class PassengerEntry {
    private final AvatarSteeringManager steeringManager;
    private final int boardingFloor;
    private final int destinationFloor;
    private Runnable runnable;
    public boolean hasBoarded;
    public boolean isQueued;

    public PassengerEntry(AvatarSteeringManager steeringManager, int boardingFloor, int destinationFloor) {
      this.steeringManager = steeringManager;
      this.boardingFloor = boardingFloor;
      this.destinationFloor = destinationFloor;
    }

    public void addCallback(Runnable runnable) {
      this.runnable = runnable;
    }
  }
}
