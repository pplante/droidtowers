package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.Overlays;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.input.PlacementTool;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.*;

public class HeadsUpDisplay extends Group {
  private TextureAtlas hudAtlas;
  private Stage guiStage;
  private Skin guiSkin;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private LabelButton addRoomButton;
  private Menu addRoomMenu;
  private Label statusLabel;
  private float updateMoneyLabel;
  private static HeadsUpDisplay instance;
  private SpriteBatch spriteBatch;
  private BitmapFont defaultBitmapFont;
  private BitmapFont menloBitmapFont;
  private Toast toast;
  private LabelButton setOverlayButton;
  private Menu overlayMenu;
  private Table topBar;
  private ToolTip mouseToolTip;

  public static HeadsUpDisplay getInstance() {
    if (instance == null) {
      instance = new HeadsUpDisplay();
    }

    return instance;
  }

  public void initialize(final OrthographicCamera camera, final GameGrid gameGrid, Stage guiStage, SpriteBatch spriteBatch) {
    this.guiStage = guiStage;
    this.spriteBatch = spriteBatch;
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.guiSkin = new Skin(Gdx.files.internal("default-skin.ui"), Gdx.files.internal("default-skin.png"));

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_16.fnt"), false);
    defaultBitmapFont = new BitmapFont(Gdx.files.internal("default.fnt"), false);

    hudAtlas = new TextureAtlas(Gdx.files.internal("hud/buttons.txt"));

    topBar = new Table();
    topBar.defaults();
    topBar.top().left();
    topBar.setBackground(guiSkin.getPatch("default-round"));

    addActor(topBar);

    topBar.row().top().left().pad(5);

    makeAddRoomButton();
    makeAddRoomMenu();

    makeOverlayButton();

    makeMoneyLabel();

    topBar.pack();

    topBar.x = 0;
    topBar.y = Gdx.graphics.getHeight() - topBar.height;

    mouseToolTip = new ToolTip();
    addActor(mouseToolTip);

    guiStage.addActor(this);
  }

  private void makeMoneyLabel() {
    statusLabel = new Label(guiSkin);
    statusLabel.setAlignment(Align.RIGHT | Align.TOP);
    addActor(statusLabel);

    updateStatusLabel();

    BitmapFont.TextBounds textBounds = statusLabel.getTextBounds();
    statusLabel.x = Gdx.graphics.getWidth() - 5;
    statusLabel.y = Gdx.graphics.getHeight() - 5;
  }

  private void updateStatusLabel() {
    Player player = Player.getInstance();
    statusLabel.setText(String.format("%d coins\n %d exp\n(%d + %d)/%d pop\n%d/%d jobs", player.getCoins(), player.getExperience(), player.getPopulationResidency(), player.getPopulationAttracted(), player.getMaxPopulation(), player.getJobsFilled(), player.getJobsMax()));
  }

  private void makeAddRoomButton() {
    addRoomButton = new LabelButton(guiSkin, "Add Room");
    addRoomButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        addRoomMenu.show(addRoomButton);
      }
    });

    topBar.add(addRoomButton);
  }

  private void makeOverlayButton() {
    setOverlayButton = new LabelButton(guiSkin, "Overlays");
//    setOverlayButton.x = 150;
//    setOverlayButton.y = Gdx.graphics.getHeight() - setOverlayButton.height - 25;

    setOverlayButton.setClickListener(new ClickListener() {
      boolean isShowing;

      public void click(Actor actor, float x, float y) {
//        if (!isShowing) {
//          guiStage.addActor(overlayMenu);
//        } else {
//          guiStage.removeActor(overlayMenu);
//        }
//
//        isShowing = !isShowing;
        overlayMenu.show(setOverlayButton);
      }
    });

    topBar.add(setOverlayButton);

    overlayMenu = new Menu(guiSkin);
    overlayMenu.defaults();
    overlayMenu.top().left();

    for (final Overlays overlay : Overlays.values()) {
      final CheckBox checkBox = new CheckBox(overlay.toString(), guiSkin);
      checkBox.align(Align.LEFT);
      checkBox.getLabelCell().pad(4);
      checkBox.invalidate();
      checkBox.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          if (checkBox.isChecked()) {
            TowerGame.getGameGridRenderer().addActiveOverlay(overlay);
          } else {
            TowerGame.getGameGridRenderer().removeActiveOverlay(overlay);
          }
        }
      });
      overlayMenu.row().left().pad(2, 6, 2, 6);
      overlayMenu.add(checkBox);
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGB565);
      pixmap.setColor(Color.GRAY);
      pixmap.fill();
      pixmap.setColor(overlay.getColor(1f));
      pixmap.fillRectangle(1, 1, 14, 14);

      Image image = new Image(new Texture(pixmap));
      overlayMenu.add(image);
    }

    overlayMenu.row().colspan(2).left().pad(6, 2, 2, 2);
    LabelButton clearAllButton = new LabelButton(guiSkin, "Clear All");
    clearAllButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        TowerGame.getGameGridRenderer().clearOverlays();

        for (Actor child : overlayMenu.getActors()) {
          if (child instanceof CheckBox) {
            ((CheckBox) child).setChecked(false);
          }
        }
      }
    });

    overlayMenu.add(clearAllButton).fill();

    overlayMenu.pack();
  }

  private void makeAddRoomMenu() {
    addRoomMenu = new Menu(guiSkin);
    addRoomMenu.defaults();
    addRoomMenu.top().left();
    addRoomMenu.pad(0);

    for (final RoomType roomType : RoomTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(roomType));
    }

    for (final CommercialType commercialType : CommercialTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(commercialType));
    }

    for (final ElevatorType elevatorType : ElevatorTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(elevatorType));
    }

    for (TransitType stairType : StairTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(stairType));
    }

    addRoomMenu.row().padTop(-1).fill();

    addRoomMenu.add(new MenuItem(guiSkin, "Sell/Delete", new ClickListener() {
      public void click(Actor actor, float x, float y) {
        InputSystem.getInstance().switchTool(GestureTool.SELL, new Runnable() {
          public void run() {
            updateButtonText("Add Room");
          }
        });

        updateButtonText("Sell/Delete");

        addRoomMenu.close();
      }
    }));

    addRoomMenu.pack();
  }

  private MenuItem makeGridObjectMenuItem(final GridObjectType gridObjectType) {
    addRoomMenu.row().padTop(-1).fill();

    return new MenuItem(guiSkin, gridObjectType.getName(), new ClickListener() {
      public void click(Actor actor, float x, float y) {
        InputSystem.getInstance().switchTool(GestureTool.PLACEMENT, new Runnable() {
          public void run() {
            updateButtonText("Add Room");
          }
        });

        updateButtonText(gridObjectType.getName());

        PlacementTool placementTool = (PlacementTool) InputSystem.getInstance().getCurrentTool();
        placementTool.setup(gridObjectType);
        placementTool.enterPurchaseMode();

        addRoomMenu.close();
      }
    });
  }

  private void updateButtonText(String buttonText) {
    addRoomButton.setText(buttonText);
    addRoomButton.invalidate();
    topBar.pack();
  }

  public Skin getGuiSkin() {
    return guiSkin;
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    updateMoneyLabel += delta;
    if (updateMoneyLabel > 0.5f) {
      updateMoneyLabel = 0f;
      updateStatusLabel();
    }
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    String infoText = String.format("fps: %d, camera(%.1f, %.1f, %.1f)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom);
    menloBitmapFont.draw(batch, infoText, 5, 23);

    float javaHeapInBytes = Gdx.app.getJavaHeap() / 1048576.0f;
    float nativeHeapInBytes = Gdx.app.getNativeHeap() / 1048576.0f;
    menloBitmapFont.draw(batch, String.format("mem: (java %.2f Mb, native %.2f Mb)", javaHeapInBytes, nativeHeapInBytes), 5, 50);
  }

  @Override
  public boolean touchMoved(float x, float y) {
    updateGridPointTooltip(x, y);

    return false;
  }

  private void updateGridPointTooltip(float x, float y) {
    Vector3 worldPoint = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()).getEndPoint(1);

    GridPoint gridPoint = gameGrid.closestGridPoint(worldPoint.x, worldPoint.y);
    GridPosition gridPosition = GridPositionCache.instance().getPosition(gridPoint);
    if (gridPosition != null) {
      mouseToolTip.visible = true;
      mouseToolTip.setText(String.format("%s\nobjects: %s\nelevator: %s\nstairs: %s", gridPoint, gridPosition.size(), gridPosition.elevator != null, gridPosition.stair != null));
      mouseToolTip.x = x;
      mouseToolTip.y = y;
    } else {
      mouseToolTip.visible = false;
    }
  }

  public void showToast(String message) {
    if (toast == null) {
      toast = new Toast();
      addActor(toast);
    }

    toast.setMessage(message);
    toast.show();
  }
}
