package com.unhappyrobot;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.unhappyrobot.achievements.Achievement;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.PathSearchManager;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.graphics.BackgroundLayer;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GameGridRenderer;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.types.CommercialTypeFactory;
import com.unhappyrobot.types.ElevatorTypeFactory;
import com.unhappyrobot.types.RoomTypeFactory;
import com.unhappyrobot.types.StairTypeFactory;
import com.unhappyrobot.utils.Random;

import java.util.List;

import static com.unhappyrobot.input.InputSystem.Keys;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;

  private static List<GameLayer> gameLayers;
  private Matrix4 matrix;

  private static GameGrid gameGrid;
  private Stage guiStage;
  private static GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private boolean loadedSavedGame;
  private FileHandle gameSaveLocation;
  private static float timeMultiplier;

  public static GameGridRenderer getGameGridRenderer() {
    return gameGridRenderer;
  }

  public void create() {
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

    TweenSystem.getTweenManager();
    AchievementEngine.instance();
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    Random.init();

    timeMultiplier = 1f;

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    guiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);

    GridPositionCache.instance();

    gameGrid = new GameGrid();
    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(50, 40);
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

    gameState = new GameState(gameGrid);

    HeadsUpDisplay.instance().initialize(camera, gameGrid, guiStage, spriteBatch);

    BackgroundLayer groundLayer = new BackgroundLayer("backgrounds/ground.png");
    groundLayer.setSize(gameGrid.getWorldSize().x, 256f);
    groundLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    BackgroundLayer skyLayer = new BackgroundLayer("backgrounds/stormysky.png");
    skyLayer.setPosition(0, 256);
    skyLayer.setSize(gameGrid.getWorldSize().x, gameGrid.getWorldSize().y - 256f);
    skyLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    gameGridRenderer = gameGrid.getRenderer();
    AvatarLayer.initialize(gameGrid);

    GameLayer testLayer = new GameLayer();
    gameLayers = Lists.newArrayList();
    gameLayers.add(skyLayer);
    gameLayers.add(new CloudLayer(gameGrid.getWorldSize()));
    gameLayers.add(groundLayer);
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.instance());

//    BEGIN INPUT SETUP:
    InputSystem.instance().setup(camera, gameLayers);
    InputSystem.instance().addInputProcessor(guiStage, 10);
    Gdx.input.setInputProcessor(InputSystem.instance());

    InputSystem.instance().bind(new int[]{Keys.PLUS, Keys.UP}, new InputCallback() {
      public boolean run(float timeDelta) {
        timeMultiplier += 0.5f;
        timeMultiplier = Math.min(timeMultiplier, 4);

        return true;
      }
    });

    InputSystem.instance().bind(new int[]{Keys.MINUS, Keys.DOWN}, new InputCallback() {
      public boolean run(float timeDelta) {
        timeMultiplier -= 0.5f;
        timeMultiplier = Math.max(timeMultiplier, 0.5f);

        return true;
      }
    });

    InputSystem.instance().bind(Keys.G, new InputCallback() {
      public boolean run(float timeDelta) {
        gameGridRenderer.toggleGridLines();

        return true;
      }
    });

    InputSystem.instance().bind(Keys.T, new InputCallback() {
      public boolean run(float timeDelta) {
        gameGridRenderer.toggleTransitLines();

        return true;
      }
    });

    InputSystem.instance().bind(Keys.NUM_0, new InputCallback() {
      public boolean run(float timeDelta) {
        camera.zoom = 1f;

        return true;
      }
    });

    InputSystem.instance().bind(Keys.R, new InputCallback() {
      public boolean run(float timeDelta) {
        Texture.invalidateAllTextures(Gdx.app);

        return true;
      }
    });

    InputSystem.instance().bind(Keys.A, new InputCallback() {
      public boolean run(float timeDelta) {
        for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
          System.out.println("achievement = " + achievement);
          System.out.println("achievement.isCompleted() = " + achievement.isCompleted());
          System.out.println("\n\n");

          if (achievement.isCompleted()) {
            achievement.giveReward();
          }
        }

        return true;
      }
    });

    InputSystem.instance().bind(new int[]{Keys.BACK, Keys.ESCAPE}, new InputCallback() {
      private Dialog exitDialog;

      public boolean run(float timeDelta) {
        if (exitDialog != null) {
          exitDialog.dismiss();
        } else {
          exitDialog = new Dialog().setTitle("Awww, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
            @Override
            public void onClick(Dialog dialog) {
              dialog.dismiss();
              Gdx.app.exit();
            }
          }).addButton(ResponseType.NEGATIVE, "No way!", new OnClickCallback() {
            @Override
            public void onClick(Dialog dialog) {
              dialog.dismiss();
            }
          }).centerOnScreen().show();

          exitDialog.onDismiss(new InputCallback() {
            public boolean run(float timeDelta) {
              exitDialog = null;
              return true;
            }
          });
        }

        return true;
      }
    });

    gameSaveLocation = Gdx.files.external(Gdx.app.getType().equals(Application.ApplicationType.Desktop) ? ".towergame/test.json" : "test.json");
    gameState.loadSavedGame(gameSaveLocation, camera);
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    camera.update();
    InputSystem.instance().update(deltaTime);
    PathSearchManager.instance().update(deltaTime);

    spriteBatch.setProjectionMatrix(camera.combined);

    updateGameObjects();

    for (GameLayer layer : gameLayers) {
      layer.render(spriteBatch, camera);
    }

    guiStage.draw();
    Table.drawDebug(guiStage);
  }

  private void updateGameObjects() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    deltaTime *= timeMultiplier;

    TweenSystem.getTweenManager().update((int) (deltaTime * 1000));
    gameState.update(deltaTime, gameGrid);
    guiStage.act(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    HeadsUpDisplay.instance().act(deltaTime);
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    Gdx.gl.glViewport(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
    gameState.saveGame(gameSaveLocation, camera);
    System.exit(0);
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    spriteBatch.dispose();
    guiStage.dispose();
  }

  public static OrthographicCamera getCamera() {
    return camera;
  }

  public static float getTimeMultiplier() {
    return timeMultiplier;
  }

  public static GameGrid getGameGrid() {
    return gameGrid;
  }
}
