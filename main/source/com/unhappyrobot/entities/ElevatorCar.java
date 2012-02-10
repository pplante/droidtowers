package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.controllers.GameObjectAccessor;
import com.unhappyrobot.events.GridObjectBoundsChangeEvent;
import com.unhappyrobot.math.GridPoint;

public class ElevatorCar extends GameObject {
  private int floor;
  private final Elevator elevator;
  private final Sprite sprite;

  public ElevatorCar(Elevator elevator, TextureAtlas elevatorAtlas) {
    this.elevator = elevator;
    elevator.eventBus().register(this);

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

  public void moveToFloor(int nextFloor) {
    nextFloor = Math.max(0, Math.min((int) elevator.getContentSize().y, nextFloor));

    GridPoint gridPoint = elevator.getContentPosition().cpy();
    gridPoint.y += nextFloor;

    Vector2 finalPosition = gridPoint.toWorldVector2(elevator.gameGrid);

    Tween.to(this, GameObjectAccessor.POSITION, 500)
            .target(finalPosition.x, finalPosition.y)
            .start(TowerGame.getTweenManager());


    floor = nextFloor;
  }

  public void draw(SpriteBatch spriteBatch) {
    sprite.setPosition(position.x, position.y);
    sprite.draw(spriteBatch);
  }
}
