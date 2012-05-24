/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PickerTool;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.*;

import java.util.Set;

import static com.happydroids.droidtowers.platform.Display.scale;

public class HeadsUpDisplay extends WidgetGroup {
  private TextureAtlas hudAtlas;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private static HeadsUpDisplay instance;
  private ToolTip mouseToolTip;
  private GridObjectPurchaseMenu purchaseDialog;
  private RadialMenu toolMenu;
  private final StackGroup notificationStack;
  private ImageButton toolButton;
  private ImageButton.ImageButtonStyle toolButtonStyle;
  private TutorialStepNotification tutorialStep;
  private final StatusBarPanel statusBarPanel;
  private final HeaderButtonBar headerButtonBar;
  private AchievementButton achievementButton;

  public HeadsUpDisplay(Stage stage, OrthographicCamera camera, CameraController cameraController, GameGrid gameGrid, AvatarLayer avatarLayer, AchievementEngine achievementEngine, TutorialEngine tutorialEngine) {
    super();

    HeadsUpDisplay.instance = this;

    notificationStack = new StackGroup();

    this.stage = stage;
    this.camera = camera;
    this.gameGrid = gameGrid;

    hudAtlas = TowerAssetManager.textureAtlas("hud/buttons.txt");

    statusBarPanel = new StatusBarPanel();
    statusBarPanel.x = 0;
    statusBarPanel.y = stage.height() - statusBarPanel.height;
    addActor(statusBarPanel);

    mouseToolTip = new ToolTip();
    addActor(mouseToolTip);
    addActor(new ExpandLandOverlay(this.gameGrid, avatarLayer, cameraController));

    buildToolButtonMenu();


    headerButtonBar = new HeaderButtonBar(hudAtlas, gameGrid);
    addActor(headerButtonBar);
    headerButtonBar.x = stage.width() - headerButtonBar.width - 10;
    headerButtonBar.y = stage.height() - headerButtonBar.height - 10;

    achievementButton = new AchievementButton(achievementEngine);
    achievementButton.x = 10;
    achievementButton.y = stage.height() - statusBarPanel.height - achievementButton.height - 10;
    achievementButton.getParticleEffect().setPosition(achievementButton.x + achievementButton.width / 2, achievementButton.y + achievementButton.height / 2);

    addActor(achievementButton);

    notificationStack.pad(10);
    notificationStack.x = 0;
    notificationStack.y = 0;
    addActor(notificationStack);

    this.stage.addActor(this);
  }

  private void buildToolButtonMenu() {
    toolMenu = new RadialMenu();
    toolMenu.arc = 35f;
    toolMenu.arcStart = -3.5f;
    toolMenu.radius = scale(180);
    toolMenu.rotation = 0;

    ImageButton housingButton = new ImageButton(hudAtlas.findRegion("tool-housing"));
    housingButton.setClickListener(makePurchaseButtonClickListener("Housing", RoomTypeFactory.instance()));


    ImageButton transitButton = new ImageButton(hudAtlas.findRegion("tool-transit"));
    transitButton.setClickListener(makePurchaseButtonClickListener("Transit", TransitTypeFactory.instance()));

    ImageButton commerceButton = new ImageButton(hudAtlas.findRegion("tool-commerce"));
    commerceButton.setClickListener(makePurchaseButtonClickListener("Commerce", CommercialTypeFactory.instance()));


    ImageButton servicesButton = new ImageButton(hudAtlas.findRegion("tool-services"));
    servicesButton.setClickListener(makePurchaseButtonClickListener("Services", ServiceRoomTypeFactory.instance()));


    final ImageButton sellButton = new ImageButton(hudAtlas.findRegion("tool-sell"));
    sellButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        toolMenu.hide();
        toolButton.setStyle(sellButton.getStyle());
        InputSystem.instance().switchTool(GestureTool.SELL, new Runnable() {
          public void run() {
            toolButton.setStyle(toolButtonStyle);
          }
        });
      }
    });

    toolMenu.addActor(housingButton);
    toolMenu.addActor(transitButton);
    toolMenu.addActor(commerceButton);
    toolMenu.addActor(servicesButton);
    toolMenu.addActor(sellButton);

    toolButton = new ImageButton(hudAtlas.findRegion("tool-sprite"));
    toolButton.x = stage.width() - toolButton.width - 10;
    toolButton.y = 10;
    addActor(toolButton);
    toolButtonStyle = toolButton.getStyle();
    toolButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        if (!toolMenu.visible) {
          stage.addActor(toolMenu);
          toolMenu.x = toolButton.x + 20f;
          toolMenu.y = toolButton.y;
          toolMenu.show();
          TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
        } else {
          toolMenu.hide();
          toolMenu.markToRemove(true);
        }
      }
    });
  }

  private ClickListener makePurchaseButtonClickListener(final String dialogTitle, final GridObjectTypeFactory typeFactory) {
    return new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        toolMenu.hide();

        if (purchaseDialog == null) {
          if (typeFactory instanceof RoomTypeFactory) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
          }

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
    purchaseDialog = new GridObjectPurchaseMenu(getStage(), title, typeFactory, new Runnable() {
      public void run() {
        toolButton.setStyle(toolButtonStyle);
      }
    });

    purchaseDialog.setDismissCallback(new Runnable() {
      public void run() {
        purchaseDialog = null;
        if (InputSystem.instance().getCurrentTool() instanceof PickerTool) {
          toolButton.setStyle(toolButtonStyle);
        }
      }
    });

    purchaseDialog.show();
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
    if (HappyDroidConsts.DEBUG) {
      Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);

      GridPoint gridPointAtMouse = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
      GridPosition gridPosition = gameGrid.positionCache().getPosition(gridPointAtMouse);
      if (gridPosition != null) {
        int totalVisitors = 0;
        int residents = 0;
        float pointNoiseLevel = gridPosition.getNoiseLevel();
        float objectNoiseLevel = 0f;
        float desirabilityLevel = 0f;
        Set<String> objectNames = Sets.newHashSet();
        for (GridObject gridObject : gridPosition.getObjects()) {
          if (gridObject instanceof CommercialSpace) {
            totalVisitors = Math.max(((CommercialSpace) gridObject).getNumVisitors(), totalVisitors);
          } else if (gridObject instanceof Room) {
            residents = ((Room) gridObject).getCurrentResidency();
          }


          objectNoiseLevel = gridObject.getNoiseLevel();
          desirabilityLevel = gridObject.getDesirability();

          objectNames.add(gridObject.getGridObjectType().getName());
        }


        mouseToolTip.visible = true;
        mouseToolTip.setText(String.format("%s\n" +
                                                   "objects: %s\n" +
                                                   "%s\n" +
                                                   "elevator: %s\n" +
                                                   "stairs: %s\n" +
                                                   "visitors: %d\n" +
                                                   "population: %d\n" +
                                                   "point noise: %.2f\n" +
                                                   "object noise: %.2f\n" +
                                                   "desirability: %.2f\n" +
                                                   "trans dist: %.0f", gridPointAtMouse,
                                                  gridPosition.size(),
                                                  Joiner.on(", ").join(objectNames),
                                                  gridPosition.elevator != null,
                                                  gridPosition.stair != null,
                                                  totalVisitors,
                                                  residents,
                                                  pointNoiseLevel,
                                                  objectNoiseLevel,
                                                  desirabilityLevel,
                                                  gridPosition.distanceFromTransit));
        mouseToolTip.x = x + 15;
        mouseToolTip.y = y + 15;
      } else {
        mouseToolTip.visible = false;
      }
    }
  }

  public static void showToast(String message, Object... objects) {
    Toast toast = new Toast();
    toast.setMessage(String.format(message, objects));
    instance().stage.addActor(toast);
    toast.show();
  }

  public float getPrefWidth() {
    return stage.width();
  }

  public float getPrefHeight() {
    return stage.height();
  }

  public void showTipBubble(GridObject gridObject, String message) {
    SpeechBubble bubble = new SpeechBubble(camera);
    bubble.setText(message);
    bubble.followObject(gridObject);
    bubble.show();
    System.out.println("Bubble: " + message);
    addActor(bubble);
  }

  public static StackGroup getNotificationStack() {
    return instance.notificationStack;
  }

  public static HeadsUpDisplay instance() {
    return instance;
  }

  public static void setTutorialStepNotification(TutorialStepNotification nextStep) {
    if (instance.tutorialStep != null) {
      instance.tutorialStep.markToRemove(true);
    }

    instance.tutorialStep = nextStep;

    if (instance.tutorialStep != null) {
      instance.getStage().addActor(instance.tutorialStep);

      instance.tutorialStep.x = 10;
      instance.tutorialStep.y = ((int) (instance.getStage().height() - (instance.statusBarPanel.height + instance.tutorialStep.height + 6)));
    }
  }

  public AchievementButton getAchievementButton() {
    return achievementButton;
  }
}
