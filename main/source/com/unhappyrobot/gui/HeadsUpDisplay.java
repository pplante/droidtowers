package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.scenes.Scene;
import com.unhappyrobot.scenes.TowerScene;
import com.unhappyrobot.types.*;

public class HeadsUpDisplay extends WidgetGroup {
  private TextureAtlas hudAtlas;
  private Skin guiSkin;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private LabelButton addRoomButton;
  private Menu addRoomMenu;
  private Label statusLabel;
  private float updateMoneyLabel;
  private static HeadsUpDisplay instance;
  private BitmapFont menloBitmapFont;
  private Toast toast;
  private LabelButton setOverlayButton;
  private Menu overlayMenu;
  private Table topBar;
  private ToolTip mouseToolTip;
  private GridObjectPurchaseMenu purchaseDialog;
  private InputCallback closeDialogCallback = null;
  private RadialMenu toolMenu;
  private final StackGroup notificationStack;
  private TextButton expandLandButton;
  private final ImageButton toolButton;
  private final ImageButton.ImageButtonStyle toolButtonStyle;

  public HeadsUpDisplay(TowerScene towerScene) {
    instance = this;

    notificationStack = new StackGroup();

    this.stage = towerScene.getStage();
    this.camera = towerScene.getCamera();
    this.gameGrid = towerScene.getGameGrid();
    guiSkin = Scene.getGuiSkin();

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);

    hudAtlas = new TextureAtlas(Gdx.files.internal("hud/buttons.txt"));

    StatusBarPanel statusBarPanel = new StatusBarPanel(guiSkin, towerScene);
    statusBarPanel.x = -1;
    statusBarPanel.y = stage.height() - statusBarPanel.height + 1;
    addActor(statusBarPanel);

    mouseToolTip = new ToolTip(guiSkin);
    addActor(mouseToolTip);

    toolMenu = new RadialMenu();
    toolMenu.arc = 35f;
    toolMenu.radius = 140f;
    toolMenu.rotation = 3f;

    addActor(new ExpandLandOverlay(gameGrid, guiSkin));

    ImageButton housingButton = new ImageButton(hudAtlas.findRegion("tool-housing"));
    housingButton.setClickListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.instance()));
    toolMenu.addActor(housingButton);

    ImageButton transitButton = new ImageButton(hudAtlas.findRegion("tool-transit"));
    transitButton.setClickListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.instance()));
    toolMenu.addActor(transitButton);

    ImageButton commerceButton = new ImageButton(hudAtlas.findRegion("tool-commerce"));
    commerceButton.setClickListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.instance()));
    toolMenu.addActor(commerceButton);

    ImageButton servicesButton = new ImageButton(hudAtlas.findRegion("tool-services"));
    servicesButton.setClickListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.instance()));
    toolMenu.addActor(servicesButton);

    final ImageButton sellButton = new ImageButton(hudAtlas.findRegion("tool-sell"));
    sellButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        toolMenu.hide();
        toolButton.setStyle(sellButton.getStyle());
        InputSystem.instance().switchTool(GestureTool.SELL, new Runnable() {
          public void run() {
            toolButton.setStyle(toolButtonStyle);
          }
        });
      }
    });

    toolMenu.addActor(sellButton);

    toolButton = new ImageButton(hudAtlas.findRegion("tool-sprite"));
    toolButton.x = stage.width() - toolButton.width - 5;
    toolButton.y = 5;
    addActor(toolButton);
    toolButtonStyle = toolButton.getStyle();
    toolButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        if (!toolMenu.visible) {
          stage.addActor(toolMenu);
          toolMenu.x = toolButton.x + 20f;
          toolMenu.y = toolButton.y;
          toolMenu.show();
        } else {
          toolMenu.hide();
          toolMenu.markToRemove(true);
        }
      }
    });

    final AudioControl audioControl = new AudioControl(hudAtlas);
    final OverlayControl overlayControl = new OverlayControl(hudAtlas, guiSkin, gameGrid.getRenderer());

    final Table topLeftButtons = new Table(getGuiSkin());
    topLeftButtons.defaults().center().right().space(5);
    topLeftButtons.clear();
    topLeftButtons.add(audioControl);
    topLeftButtons.add(overlayControl);

    topLeftButtons.x = stage.width() - topLeftButtons.width - 5;
    topLeftButtons.y = stage.height() - topLeftButtons.height - 5;
    addActor(topLeftButtons);

    notificationStack.pad(10);
    notificationStack.x = 0;
    notificationStack.y = 0;
    addActor(notificationStack);

    stage.addActor(this);
  }

  private void layoutTopLeftButtons(Table topLeftButtons, TextButton connectFacebookButton, AudioControl audioControl, OverlayControl overlayControl) {
    topLeftButtons.clear();
    topLeftButtons.add(connectFacebookButton);
    topLeftButtons.add(audioControl);
    topLeftButtons.add(overlayControl);
    topLeftButtons.pack();
  }

  private ClickListener makePurchaseButtonClickListener(final String dialogTitle, final GridObjectTypeFactory typeFactory) {
    return new ClickListener() {
      public void click(Actor actor, float x, float y) {
        toolMenu.hide();

        if (purchaseDialog == null) {
          makePurchaseDialog(dialogTitle, typeFactory, ((ImageButton) actor).getStyle());
          toolButton.setStyle(((ImageButton) actor).getStyle());
        } else {
          purchaseDialog.dismiss();
          purchaseDialog = null;
        }
      }
    };
  }

  private void makePurchaseDialog(String title, GridObjectTypeFactory typeFactory, ImageButton.ImageButtonStyle style) {
    purchaseDialog = new GridObjectPurchaseMenu(getStage(), getGuiSkin(), title, typeFactory, new Runnable() {
      public void run() {
        toolButton.setStyle(toolButtonStyle);
      }
    });

    purchaseDialog.setDismissCallback(new Runnable() {
      public void run() {
        purchaseDialog = null;
        if (InputSystem.instance().getCurrentTool() == GestureTool.PICKER) {
          toolButton.setStyle(toolButtonStyle);
        }
      }
    });

    stage.addActor(purchaseDialog);

    purchaseDialog.centerOnStage().modal(true).show();
  }

  public Skin getGuiSkin() {
    return guiSkin;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);
  }

  @Override
  public boolean touchMoved(float x, float y) {
    Actor hit = hit(x, y);
    if (hit == null || hit == mouseToolTip) {
      updateGridPointTooltip(x, y);
    } else {
      mouseToolTip.visible = false;
    }

    return super.touchMoved(x, y);
  }

  private void updateGridPointTooltip(float x, float y) {
    Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);

    GridPoint gridPointAtMouse = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
    GridPosition gridPosition = GridPositionCache.instance().getPosition(gridPointAtMouse);
    if (gridPosition != null) {
      int totalVisitors = 0;
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof CommercialSpace) {
          totalVisitors = ((CommercialSpace) gridObject).getNumVisitors();
        }
      }

      mouseToolTip.visible = true;
      mouseToolTip.setText(String.format("%s\nobjects: %s\nelevator: %s\nstairs: %s\nvisitors: %d", gridPointAtMouse, gridPosition.size(), gridPosition.elevator != null, gridPosition.stair != null, totalVisitors));
      mouseToolTip.x = x + 5;
      mouseToolTip.y = y + 5;
    } else {
      mouseToolTip.visible = false;
    }
  }

  public void showToast(String message, Object... objects) {
    if (toast == null) {
      toast = new Toast();
      addActor(toast);
    }

    toast.setMessage(String.format(message, objects));
    toast.show();
  }

  public float getPrefWidth() {
    return 0;
  }

  public float getPrefHeight() {
    return 0;
  }

  public void showTipBubble(GridObject gridObject, String message) {
    SpeechBubble bubble = new SpeechBubble();
    bubble.setText(message);
    bubble.followObject(gridObject);
    bubble.show();

    addActor(bubble);
  }

  public StackGroup getNotificationStack() {
    return notificationStack;
  }

  public static HeadsUpDisplay instance() {
    return instance;
  }
}
