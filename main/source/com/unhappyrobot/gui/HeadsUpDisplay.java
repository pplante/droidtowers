package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.*;

public class HeadsUpDisplay extends WidgetGroup {
  public static final float ONE_MEGABYTE = 1048576.0f;
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

  public static HeadsUpDisplay getInstance() {
    if (instance == null) {
      instance = new HeadsUpDisplay();
    }

    return instance;
  }

  public void initialize(final OrthographicCamera camera, final GameGrid gameGrid, final Stage stage, SpriteBatch spriteBatch) {
    this.stage = stage;
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.guiSkin = new Skin(Gdx.files.internal("default-skin.ui"), Gdx.files.internal("default-skin.png"));

    ModalOverlay.initialize(this);

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);

    hudAtlas = new TextureAtlas(Gdx.files.internal("hud/buttons.txt"));

    StatusBarPanel statusBarPanel = new StatusBarPanel(guiSkin);
    statusBarPanel.x = -1;
    statusBarPanel.y = stage.height() - statusBarPanel.height + 1;
    addActor(statusBarPanel);

    mouseToolTip = new ToolTip();
    addActor(mouseToolTip);

    toolMenu = new RadialMenu();
    toolMenu.arc = 33f;
    toolMenu.radius = 140f;

    ImageButton housingButton = new ImageButton(hudAtlas.findRegion("tool-housing"));
    housingButton.setClickListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.getInstance()));
    toolMenu.addActor(housingButton);

    ImageButton transitButton = new ImageButton(hudAtlas.findRegion("tool-transit"));
    transitButton.setClickListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.getInstance()));
    toolMenu.addActor(transitButton);

    ImageButton commerceButton = new ImageButton(hudAtlas.findRegion("tool-commerce"));
    commerceButton.setClickListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.getInstance()));
    toolMenu.addActor(commerceButton);

    ImageButton servicesButton = new ImageButton(hudAtlas.findRegion("tool-services"));
    servicesButton.setClickListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.getInstance()));
    toolMenu.addActor(servicesButton);

    final ImageButton toolButton = new ImageButton(hudAtlas.findRegion("tool-sprite"));
    toolButton.x = stage.width() - toolButton.width - 5;
    toolButton.y = 5;
    addActor(toolButton);
    toolButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        if (InputSystem.getInstance().getCurrentTool() != GestureTool.PLACEMENT) {
          if (purchaseDialog != null) {
            purchaseDialog.dismiss();
            purchaseDialog = null;
          }
          InputSystem.getInstance().switchTool(GestureTool.PICKER, null);
        }

        if (!toolMenu.visible) {
          toolButton.add(toolMenu);
          toolMenu.x = -200f;
          toolMenu.y = -100f;
          toolMenu.show();
        } else {
          toolMenu.hide();
        }
      }
    });

    AudioControl audioControl = new AudioControl(hudAtlas);
    audioControl.x = stage.width() - audioControl.width - 5;
    audioControl.y = stage.height() - audioControl.height - 5;
    addActor(audioControl);

    OverlayControl overlayControl = new OverlayControl(hudAtlas, guiSkin);
    overlayControl.x = audioControl.x - audioControl.width - 5;
    overlayControl.y = stage.height() - overlayControl.height - 5;
    addActor(overlayControl);

    stage.addActor(this);
  }

  private ClickListener makePurchaseButtonClickListener(final String dialogTitle, final GridObjectTypeFactory typeFactory) {
    return new ClickListener() {
      public void click(Actor actor, float x, float y) {
        toolMenu.hide();

        if (purchaseDialog == null) {
          makePurchaseDialog(dialogTitle, typeFactory);
        } else {
          purchaseDialog.dismiss();
          purchaseDialog = null;
        }
      }
    };
  }

  private void makePurchaseDialog(String title, GridObjectTypeFactory typeFactory) {
    purchaseDialog = new GridObjectPurchaseMenu(title, typeFactory);

    purchaseDialog.setDismissCallback(new Runnable() {
      public void run() {
        purchaseDialog = null;
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

    float javaHeapInBytes = Gdx.app.getJavaHeap() / ONE_MEGABYTE;
    float nativeHeapInBytes = Gdx.app.getNativeHeap() / ONE_MEGABYTE;

    String infoText = String.format("fps: %02d, camera(%.1f, %.1f, %.1f)\nmem: (java %.1f Mb, native %.1f Mb)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom, javaHeapInBytes, nativeHeapInBytes);
    menloBitmapFont.drawMultiLine(batch, infoText, 5, 35);
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
}
