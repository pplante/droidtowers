package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.unhappyrobot.Overlays;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.input.PlacementTool;
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
    makeAddRoomButton();
    makeAddRoomMenu();

    makeOverlayButton();

    makeMoneyLabel();

    this.guiStage.addActor(this);
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
    addRoomButton.x = 10;
    addRoomButton.y = Gdx.graphics.getHeight() - addRoomButton.height - 25;

    addRoomButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        addRoomMenu.show(addRoomButton);
      }
    });

    addActor(addRoomButton);
  }

  private void makeOverlayButton() {
    setOverlayButton = new LabelButton(guiSkin, "Overlays");
    setOverlayButton.x = 150;
    setOverlayButton.y = Gdx.graphics.getHeight() - setOverlayButton.height - 25;

    setOverlayButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        overlayMenu.show(setOverlayButton);
      }
    });

    addActor(setOverlayButton);

    overlayMenu = new Menu();
    overlayMenu.defaults();
    overlayMenu.top().left();

    for (final Overlays overlay : Overlays.values()) {
      overlayMenu.add(new MenuItem(guiSkin, overlay.toString(), new ClickListener() {
        public void click(Actor actor, float x, float y) {
          TowerGame.getGameGridRenderer().setActiveOverlay(overlay);

          overlayMenu.close();
        }
      }));
    }
  }

  private void makeAddRoomMenu() {
    addRoomMenu = new Menu();
    addRoomMenu.defaults();
    addRoomMenu.top().left();

    for (final RoomType roomType : RoomTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(roomType));
    }

    for (final CommercialType commercialType : CommercialTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(commercialType));
    }

    for (final ElevatorType elevatorType : ElevatorTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(elevatorType));
    }

    for (StairType stairType : StairTypeFactory.getInstance().all()) {
      addRoomMenu.add(makeGridObjectMenuItem(stairType));
    }
  }

  private MenuItem makeGridObjectMenuItem(final GridObjectType gridObjectType) {
    return new MenuItem(guiSkin, gridObjectType.getName(), new ClickListener() {
      public void click(Actor actor, float x, float y) {
        InputSystem.getInstance().switchTool(GestureTool.PLACEMENT, new Runnable() {
          public void run() {
            addRoomButton.setText("Add Room");
          }
        });

        addRoomButton.setText(gridObjectType.getName());

        PlacementTool placementTool = (PlacementTool) InputSystem.getInstance().getCurrentTool();
        placementTool.setup(gridObjectType);
        placementTool.enterPurchaseMode();

        addRoomMenu.close();
      }
    });
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

  public void showToast(String message) {
    if (toast == null) {
      toast = new Toast();
      addActor(toast);
    }

    toast.setMessage(message);
    toast.show();
  }
}
