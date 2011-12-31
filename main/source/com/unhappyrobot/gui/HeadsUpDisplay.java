package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.input.PlacementTool;
import com.unhappyrobot.types.RoomType;
import com.unhappyrobot.types.RoomTypeFactory;

public class HeadsUpDisplay extends Group {
  private TextureAtlas hudAtlas;
  private Stage stage;
  private Skin skin;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private LabelButton addRoomButton;
  private Menu addRoomMenu;
  private Label moneyLabel;
  private float updateMoneyLabel;
  private static HeadsUpDisplay instance;

  public static HeadsUpDisplay getInstance() {
    if (instance == null) {
      instance = new HeadsUpDisplay();
    }

    return instance;
  }

  public void initialize(Stage stage, Skin skin, final OrthographicCamera camera, final GameGrid gameGrid) {
    this.stage = stage;
    this.skin = skin;
    this.camera = camera;
    this.gameGrid = gameGrid;

    hudAtlas = new TextureAtlas(Gdx.files.internal("hud/buttons.txt"));
    makeAddRoomButton();
    makeAddRoomMenu();
    makeMoneyLabel();

    stage.addActor(this);
  }

  private void makeMoneyLabel() {
    moneyLabel = new Label(skin);
    moneyLabel.setAlignment(Align.RIGHT | Align.TOP);
    addActor(moneyLabel);

    updateMoneyLabel();

    BitmapFont.TextBounds textBounds = moneyLabel.getTextBounds();
    moneyLabel.x = Gdx.graphics.getWidth() - 5;
    moneyLabel.y = Gdx.graphics.getHeight() - 5;
  }

  private void updateMoneyLabel() {
    moneyLabel.setText(String.format("%d coins / %d gold", Player.getInstance().getCoins(), Player.getInstance().getGold()));
  }

  private void makeAddRoomButton() {
    addRoomButton = new LabelButton(skin, "Add Room");
    addRoomButton.defaults();
    addRoomButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        addRoomMenu.show(addRoomButton);
      }
    });
    addRoomButton.pack();
    addRoomButton.x = 10;
    addRoomButton.y = Gdx.graphics.getHeight() - addRoomButton.height - 10;
    addActor(addRoomButton);
  }

  private void makeAddRoomMenu() {
    addRoomMenu = new Menu();
    addRoomMenu.defaults();
    addRoomMenu.top().left();

    for (final RoomType roomType : RoomTypeFactory.getInstance().all()) {
      addRoomMenu.add(new MenuItem(skin, roomType.getName(), new ClickListener() {
        public void click(Actor actor, float x, float y) {
          InputSystem.getInstance().switchTool(GestureTool.PLACEMENT, new Runnable() {
            public void run() {
              addRoomButton.setText("Add Room");
            }
          });

          addRoomButton.setText(roomType.getName());

          PlacementTool placementTool = (PlacementTool) InputSystem.getInstance().getCurrentTool();
          placementTool.setup(camera, gameGrid, roomType);
          placementTool.enterPurchaseMode();

          addRoomMenu.close();
        }
      }));
    }
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    updateMoneyLabel += delta;
    if (updateMoneyLabel > 0.5f) {
      updateMoneyLabel = 0f;
      updateMoneyLabel();
    }
  }

  public Skin getSkin() {
    return skin;
  }
}
