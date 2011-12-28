package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.money.PurchaseManager;
import com.unhappyrobot.types.GridObjectType;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class PlacementTool implements GestureListener {
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private GridObjectType gridObjectType;
  private GridObject gridObject;
  private Vector2 touchDownPointDelta;
  private boolean isDraggingGridObject;
  private PurchaseManager purchaseManager;

  public void setup(OrthographicCamera camera, GameGrid gameGrid, GridObjectType gridObjectType) {
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.gridObjectType = gridObjectType;
    this.gridObjectType.makeGridObject();
  }

  private Vector2 findGameGridPointAtFinger() {
    Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
    Vector3 endPoint = pickRay.getEndPoint(1);
    return gameGrid.convertScreenPointToGridPoint(endPoint.x, endPoint.y);
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector2 gridPointAtFinger = findGameGridPointAtFinger();
    if (gridObject == null) {
      gridObject = gridObjectType.makeGridObject();
      gridObject.position.set(gameGrid.clampPosition(gridPointAtFinger, gridObject.size));

      gameGrid.addObject(gridObject);
    } else {
      touchDownPointDelta = gridPointAtFinger.cpy().sub(gridObject.position);
    }

    isDraggingGridObject = gridObject.getBounds().contains(gridPointAtFinger);

    return true;
  }

  public boolean tap(int x, int y, int count) {
    if (count >= 2) {
      if (purchaseManager != null) {
        purchaseManager.makePurchase();
      }

      gridObject = null;
      touchDownPointDelta = null;

      verifyAbilityToPurchase();
    }

    return false;
  }

  private void verifyAbilityToPurchase() {
    if (purchaseManager != null && !purchaseManager.canPurchase()) {
      InputSystem.getInstance().switchTool(GestureTool.NONE, null);
    }
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    if (isDraggingGridObject) {
      Vector2 gridPointAtFinger = findGameGridPointAtFinger();

      if (touchDownPointDelta != null) {
        gridPointAtFinger.sub(touchDownPointDelta);
      }

      gridObject.position.set(gameGrid.clampPosition(gridPointAtFinger, gridObject.size));

      if (gameGrid.canObjectBeAt(gridObject)) {
        gridObject.getSprite().setColor(Color.WHITE);
      } else {
        gridObject.getSprite().setColor(Color.RED);
      }

      return true;
    }

    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    return false;
  }

  public void enterPurchaseMode() {
    purchaseManager = new PurchaseManager(gridObjectType);

    verifyAbilityToPurchase();
  }
}
