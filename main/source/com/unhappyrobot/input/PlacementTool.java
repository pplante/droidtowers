package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.money.PurchaseManager;
import com.unhappyrobot.types.GridObjectType;

import static com.unhappyrobot.input.InputSystem.Keys;

public class PlacementTool extends ToolBase {
  private GridObjectType gridObjectType;
  private GridObject gridObject;
  private Vector2 touchDownPointDelta;
  private boolean isDraggingGridObject;
  private PurchaseManager purchaseManager;
  private final Action cancelPlacementAction;

  public PlacementTool(OrthographicCamera camera, GameGrid gameGrid) {
    super(camera, gameGrid);

    cancelPlacementAction = new Action() {
      public boolean run(float timeDelta) {
        InputSystem.getInstance().switchTool(GestureTool.PICKER, null);
        return false;
      }
    };

    InputSystem.getInstance().bind(new int[]{Keys.ESCAPE, Keys.BACK}, cancelPlacementAction);
  }

  public void setup(GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector2 gridPointAtFinger = gridPointAtFinger();
    if (gridObject == null) {
      gridObject = gridObjectType.makeGridObject(gameGrid);
      gridObject.position.set(gameGrid.clampPosition(gridPointAtFinger, gridObject.size));

      gameGrid.addObject(gridObject);

      updateGridObjectStatus();
    } else {
      touchDownPointDelta = gridPointAtFinger.cpy().sub(gridObject.position);
    }

    isDraggingGridObject = gridObject.getBounds().contains(gridPointAtFinger);

    return true;
  }

  public boolean tap(int x, int y, int count) {
    if (count >= 2) {
      if (!gameGrid.canObjectBeAt(gridObject)) {
        new Dialog()
                .setTitle("Invalid Position")
                .setMessage("This object cannot be placed here.")
                .addButton("Okay")
                .centerOnScreen()
                .show();
        return false;
      }

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
      InputSystem.getInstance().switchTool(GestureTool.PICKER, null);
    }
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    if (isDraggingGridObject) {
      Vector2 gridPointAtFinger = gridPointAtFinger();

      if (touchDownPointDelta != null) {
        gridPointAtFinger.sub(touchDownPointDelta);
      }

      gridObject.position.set(gameGrid.clampPosition(gridPointAtFinger, gridObject.size));

      updateGridObjectStatus();

      return true;
    }

    return false;
  }

  private void updateGridObjectStatus() {
    if (gameGrid.canObjectBeAt(gridObject)) {
      gridObject.getSprite().setColor(Color.WHITE);
    } else {
      gridObject.getSprite().setColor(Color.RED);
    }
  }

  public void enterPurchaseMode() {
    purchaseManager = new PurchaseManager(gridObjectType);

    verifyAbilityToPurchase();
  }

  @Override
  public void cleanup() {
    if (gridObject != null) {
      gameGrid.removeObject(gridObject);
    }

    InputSystem.getInstance().unbind(new int[]{Keys.ESCAPE, Keys.BACK}, cancelPlacementAction);
  }
}
