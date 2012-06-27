/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.gui.PurchaseDroidTowersUnlimitedPrompt;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.money.GridObjectPurchaseChecker;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.platform.Platform;

import java.util.List;

import static com.happydroids.droidtowers.TowerConsts.LIMITED_VERSION_MAX_FLOOR;
import static com.happydroids.droidtowers.TowerConsts.LOBBY_FLOOR;
import static com.happydroids.droidtowers.input.InputSystem.Keys;
import static com.happydroids.droidtowers.types.ProviderType.SKY_LOBBY;

public class PlacementTool extends ToolBase {
  private GridObjectType gridObjectType;
  private GridObject gridObject;
  private GridPoint touchDownPointDelta;
  private boolean isDraggingGridObject;
  private GridObjectPurchaseChecker gridObjectPurchaseChecker;
  private final InputCallback cancelPlacementInputCallback;

  public PlacementTool(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid) {
    super(camera, gameLayers, gameGrid);

    cancelPlacementInputCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        InputSystem.instance().switchTool(GestureTool.PICKER, null);
        return true;
      }
    };

    InputSystem.instance().bind(new int[]{Keys.ESCAPE, Keys.BACK}, cancelPlacementInputCallback);
  }

  public void setup(GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector3 worldPoint = camera.getPickRay(x, y).getEndPoint(1);
    GridPoint gridPointAtFinger = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
    makeGridObjectAtFinger_whenGridObjectIsNull(gridPointAtFinger);

    isDraggingGridObject = gridObject.getWorldBounds().contains(worldPoint.x, worldPoint.y);

    return true;
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    if (isDraggingGridObject) {
      Vector3 worldPoint = camera.getPickRay(x, y).getEndPoint(1);
      Vector3 deltaPoint = camera.getPickRay(x + -deltaX, y + deltaY).getEndPoint(1);
      GridPoint gridPointAtFinger = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);

      if (touchDownPointDelta != null) {
        gridPointAtFinger.sub(touchDownPointDelta);
      }
      if (gridObject != null) {
        gridObject.setPosition(gridPointAtFinger);
      }

      return true;
    }

    return false;
  }

  @Override
  public void update(float deltaTime) {
    if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
      Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);
      GridPoint gridPointAtFinger = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);

      makeGridObjectAtFinger_whenGridObjectIsNull(gridPointAtFinger);

      if (gridObject != null) {
        gridObject.setPosition(gridPointAtFinger);
      }
    }
  }

  private void makeGridObjectAtFinger_whenGridObjectIsNull(GridPoint gridPointAtFinger) {
    if (gridObject == null) {
      gridObject = gridObjectType.makeGridObject(gameGrid);
      gridObject.setPosition(gridPointAtFinger);

      gameGrid.addObject(gridObject);
    } else {
      touchDownPointDelta = gridPointAtFinger.cpy().sub(gridObject.getPosition());
    }
  }

  public boolean tap(int x, int y, int count) {
    switch (Gdx.app.getType()) {
      case Android:
        return count >= 2 && finishPurchase();
      case Desktop:
        return count >= 1 && finishPurchase();
    }

    return false;
  }

  private boolean finishPurchase() {
    if (gridObject != null) {
      if (gridObject.getPosition().y > LIMITED_VERSION_MAX_FLOOR && !Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
        new PurchaseDroidTowersUnlimitedPrompt()
                .setMessage("Sorry, but the technology to build Towers higher than " + (LIMITED_VERSION_MAX_FLOOR - LOBBY_FLOOR) + " floors requires Droid Towers: Unlimited version.\n\nWould you like to unlock it?")
                .show();
        return true;
      } else if (!gameGrid.canObjectBeAt(gridObject)) {
        HeadsUpDisplay.showToast(gridObjectType.provides(SKY_LOBBY) ? "The Sky Lobby can only be built every 15 floors." : "This object cannot be placed here.");
        return true;
      } else {
        gridObject.setPlaced(true);
      }

      if (gridObjectPurchaseChecker != null) {
        gridObjectPurchaseChecker.makePurchase();
      }
    }

    gridObject = null;
    touchDownPointDelta = null;

    verifyAbilityToPurchase();
    return false;
  }

  private void verifyAbilityToPurchase() {
    if (gridObjectPurchaseChecker != null && !gridObjectPurchaseChecker.canPurchase()) {
      InputSystem.instance().switchTool(GestureTool.PICKER, null);
    }
  }

  public void enterPurchaseMode() {
    gridObjectPurchaseChecker = new GridObjectPurchaseChecker(gridObjectType);

    verifyAbilityToPurchase();
  }

  @Override
  public void cleanup() {
    if (gridObject != null) {
      gameGrid.removeObject(gridObject);
      gridObject = null;
    }

    InputSystem.instance().unbind(new int[]{Keys.ESCAPE, Keys.BACK}, cancelPlacementInputCallback);
  }
}
