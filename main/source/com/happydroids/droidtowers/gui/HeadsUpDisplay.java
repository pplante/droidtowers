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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.input.PickerTool;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.scenes.ViewNeighborSplashScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import java.util.Set;

import static com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import static com.happydroids.HappyDroidConsts.DEBUG;
import static com.happydroids.HappyDroidConsts.DISPLAY_DEBUG_INFO;

public class HeadsUpDisplay extends WidgetGroup {
  private static final String TAG = HeadsUpDisplay.class.getSimpleName();

  private TextureAtlas hudAtlas;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private static HeadsUpDisplay instance;
  private ToolTip mouseToolTip;
  private GridObjectPurchaseMenu purchaseDialog;
  private RadialMenu toolMenu;
  private final StackGroup notificationStack;
  private HudToolButton toolButton;
  private ImageButtonStyle toolButtonStyle;
  private TutorialStepNotification tutorialStep;
  private final StatusBarPanel statusBarPanel;
  private final HeaderButtonBar headerButtonBar;
  private AchievementButton achievementButton;
  private final ImageButton viewNeighborsButton;


  public HeadsUpDisplay(Stage stage, OrthographicCamera camera, CameraController cameraController, GameGrid gameGrid, AvatarLayer avatarLayer, AchievementEngine achievementEngine, TutorialEngine tutorialEngine, final GameState gameState) {
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

    achievementButton = new AchievementButton(hudAtlas, achievementEngine);
    achievementButton.x = 10;
    achievementButton.y = stage.height() - statusBarPanel.height - achievementButton.height - 10;
    achievementButton.getParticleEffect().setPosition(achievementButton.x + achievementButton.width / 2, achievementButton.y + achievementButton.height / 2);

    addActor(achievementButton);

    viewNeighborsButton = TowerAssetManager.imageButton(hudAtlas.findRegion("view-neighbors"));
    viewNeighborsButton.layout();
    viewNeighborsButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        SceneManager.pushScene(ViewNeighborSplashScene.class, gameState);
      }
    });

    viewNeighborsButton.visible = false;
    viewNeighborsButton.x = 10;
    viewNeighborsButton.y = stage.height() - statusBarPanel.height - achievementButton.height - viewNeighborsButton.height - 20;

    addActor(viewNeighborsButton);

    notificationStack.pad(10);
    notificationStack.x = 0;
    notificationStack.y = 0;
    addActor(notificationStack);

    this.stage.addActor(this);
  }

  private void buildToolButtonMenu() {
    toolButton = new HudToolButton(hudAtlas);
    toolButton.x = stage.right() - toolButton.width - 10;
    toolButton.y = 10;
    addActor(toolButton);

    toolMenu = new ToolMenu(hudAtlas, toolButton);


    toolButtonStyle = toolButton.getStyle();
    toolButton.setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        Gdx.app.log(TAG, "Current tool: " + InputSystem.instance().getCurrentTool());
        if (InputSystem.instance().getCurrentTool() instanceof PickerTool) {
          if (toolMenu.visible) {
            toolMenu.close();
            toolMenu.markToRemove(true);
          } else {
            stage.addActor(toolMenu);
            toolMenu.x = toolButton.x + 20f;
            toolMenu.y = toolButton.y;
            toolMenu.show();
            TutorialEngine.instance().moveToStepWhenReady("tutorial-unlock-lobby");
          }
        } else {
          InputSystem.instance().switchTool(GestureTool.PICKER, null);
        }
      }
    });
  }

  @Override
  public boolean touchMoved(float x, float y) {
    //noinspection PointlessBooleanExpression
    if (DEBUG && DISPLAY_DEBUG_INFO) {
      Actor hit = hit(x, y);
      if (hit == null || hit == mouseToolTip) {
        updateGridPointTooltip(x, y);
      } else {
        mouseToolTip.visible = false;
      }
    }

    return super.touchMoved(x, y);
  }

  private void updateGridPointTooltip(float x, float y) {
    Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);

    GridPoint gridPointAtMouse = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
    GridPosition gridPosition = gameGrid.positionCache().getPosition(gridPointAtMouse);
    if (gridPosition != null) {
      int totalVisitors = 0;
      int residents = 0;
      float objectNoiseLevel = 0f;
      float desirabilityLevel = 0f;
      float objectCrimeLevel = 0f;
      Set<String> objectNames = Sets.newHashSet();
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof CommercialSpace) {
          totalVisitors = Math.max(gridObject.getNumVisitors(), totalVisitors);
        } else if (gridObject instanceof Room) {
          residents = ((Room) gridObject).getCurrentResidency();
        }

        objectNoiseLevel = gridObject.getNoiseLevel();
        desirabilityLevel = gridObject.getDesirability();
        objectCrimeLevel = gridObject.getCrimeLevel();

        objectNames.add(gridObject.getGridObjectType().getName());
      }


      mouseToolTip.visible = true;
      mouseToolTip.setText(String.format("%s\n" +
                                                 "objects: %s\n" +
                                                 "%s\n" +
                                                 "transit: %s\n" +
                                                 "security: %s\n" +
                                                 "elevator: %s\n" +
                                                 "stairs: %s\n" +
                                                 "visitors: %d\n" +
                                                 "population: %d\n" +
                                                 "point crime: %.2f\n" +
                                                 "object crime: %.2f\n" +
                                                 "point noise: %.2f\n" +
                                                 "object noise: %.2f\n" +
                                                 "desirability: %.2f\n" +
                                                 "trans dist: %.0f\n" +
                                                 "security dist: %.0f",
                                                gridPointAtMouse,
                                                gridPosition.size(),
                                                Joiner.on(", ").join(objectNames),
                                                gridPosition.connectedToTransit,
                                                gridPosition.connectedToSecurity,
                                                gridPosition.elevator != null,
                                                gridPosition.stair != null,
                                                totalVisitors,
                                                residents,
                                                gridPosition.getCrimeLevel(),
                                                objectCrimeLevel,
                                                gridPosition.getNoiseLevel(),
                                                objectNoiseLevel,
                                                desirabilityLevel,
                                                gridPosition.distanceFromTransit,
                                                gridPosition.distanceFromSecurity));
      mouseToolTip.x = x + 15;
      mouseToolTip.y = y + 15;
    } else {
      mouseToolTip.visible = false;
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

  public ImageButton getViewNeighborsButton() {
    return viewNeighborsButton;
  }
}
