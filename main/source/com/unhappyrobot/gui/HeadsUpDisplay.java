package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.RoomType;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.input.PlacementTool;
import com.unhappyrobot.types.RoomTypeFactory;

public class HeadsUpDisplay extends Table {
  private final TextureAtlas hudAtlas;
  private Skin skin;
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private LabelButton addRoomButton;
  private Menu addRoomMenu;

  public HeadsUpDisplay(Skin skin, final OrthographicCamera camera, final GameGrid gameGrid) {
    super(skin);
    this.skin = skin;
    this.camera = camera;
    this.gameGrid = gameGrid;

    defaults().top().left();

    x = 0;
    y = Gdx.graphics.getHeight();

    hudAtlas = new TextureAtlas(Gdx.files.internal("hud/buttons.txt"));
    makeAddRoomButton();
    makeAddRoomMenu();
  }

  private void makeAddRoomButton() {
    addRoomButton = new LabelButton(skin, "Add Room");
    addRoomButton.defaults().top().left();
    addRoomButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        addRoomMenu.show(addRoomButton);
      }
    });

    add(addRoomButton);
  }

  private void makeAddRoomMenu() {
    addRoomMenu = new Menu();
    addRoomMenu.defaults().top().left();

    for (final RoomType roomType : RoomTypeFactory.all()) {
      addRoomMenu.add(new MenuItem(skin, roomType.getName(), new ClickListener() {
        public void click(Actor actor, float x, float y) {
          InputSystem.getInstance().switchTool(GestureTool.PLACEMENT, new Runnable() {
            public void run() {
              addRoomButton.setText("Add Room");
            }
          });
          PlacementTool placementTool = (PlacementTool) InputSystem.getInstance().getCurrentTool();
          placementTool.setup(camera, gameGrid, roomType);

          addRoomMenu.close();
          addRoomButton.setText(roomType.getName());
        }
      }));
    }
  }
}
