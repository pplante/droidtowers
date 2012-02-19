package com.unhappyrobot.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.input.PlacementTool;
import com.unhappyrobot.types.GridObjectType;
import com.unhappyrobot.types.GridObjectTypeFactory;

public class GridObjectPurchaseMenu extends Window {
  private Class gridObjectTypeClass;
  private final Skin skin;
  private InputCallback closeDialogCallback;
  private Runnable dismissCallback;
  private boolean modalState;
  private static final int[] DIALOG_CLOSE_KEYCODES = new int[]{InputSystem.Keys.ESCAPE, InputSystem.Keys.BACK};

  public GridObjectPurchaseMenu(String objectTypeName, GridObjectTypeFactory typeFactory) {
    super("Purchase " + objectTypeName, HeadsUpDisplay.getInstance().getGuiSkin());
    skin = HeadsUpDisplay.getInstance().getGuiSkin();

    defaults().align(Align.LEFT);
    row().pad(10);

    Table container = newTable();
    container.row();

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(container);
    add(scrollPane).maxWidth(500).maxHeight(300);

    float biggestWidth = 0;
    for (Object o : typeFactory.all()) {
      GridObjectType casted = typeFactory.castToObjectType(o);

      container.row();
      container.add(new GridObjectPurchaseItem(casted)).align(Align.LEFT | Align.TOP).padBottom(4);
    }

    TextButton closeButton = new TextButton("Close", skin);
    closeButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    row().align(Align.RIGHT);
    add(closeButton).align(Align.RIGHT).pad(5);

    container.pack();
    scrollPane.pack();
    pack();
  }

  public void show() {
    closeDialogCallback = new InputCallback() {
      public boolean run(float timeDelta) {
        GridObjectPurchaseMenu.this.dismiss();
        return true;
      }
    };

    InputSystem.getInstance().bind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);
  }

  public void dismiss() {
    if (closeDialogCallback != null) {
      InputSystem.getInstance().unbind(DIALOG_CLOSE_KEYCODES, closeDialogCallback);
      closeDialogCallback = null;
    }

    if (dismissCallback != null) {
      dismissCallback.run();
    }

    if (modalState) {
      ModalOverlay.instance().hide();
    }

    markToRemove(true);
  }

  public void setDismissCallback(Runnable dismissCallback) {
    this.dismissCallback = dismissCallback;
  }

  public GridObjectPurchaseMenu centerOnStage() {
    x = (stage.width() - width) / 2;
    y = (stage.height() - height) / 2;

    return this;
  }

  public GridObjectPurchaseMenu modal(boolean newState) {
    this.modalState = newState;

    if (modalState) {
      setModal(true);
      setMovable(false);
      ModalOverlay.instance().show();
    } else {
      setModal(false);
      setMovable(true);
      ModalOverlay.instance().hide();
    }

    return this;
  }

  private class GridObjectPurchaseItem extends Table {
    public GridObjectPurchaseItem(final GridObjectType gridObjectType) {
      TextButton buyButton = new TextButton("Buy", skin);
      buyButton.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          InputSystem.getInstance().switchTool(GestureTool.PLACEMENT, null);

          PlacementTool placementTool = (PlacementTool) InputSystem.getInstance().getCurrentTool();
          placementTool.setup(gridObjectType);
          placementTool.enterPurchaseMode();

          GridObjectPurchaseMenu.this.dismiss();
        }
      });


      defaults().align(Align.LEFT | Align.TOP).pad(2);

      row().fill();
      add(new Label(gridObjectType.getName(), skin)).minWidth(200);
      add(new Label(String.format("¢ %d", gridObjectType.getCoins()), skin)).align(Align.RIGHT);

      row().align(Align.LEFT);
      TextureRegion textureRegion = gridObjectType.getTextureRegion();
      Actor actor;
      if (textureRegion != null) {
        actor = new Image(textureRegion, Scaling.fit, Align.LEFT | Align.TOP);
      } else {
        actor = new Label("No image found.", skin);
      }
      add(actor).maxHeight(40).maxWidth(200);
      add(buyButton).align(Align.RIGHT).scaling("none");
    }
  }
}
