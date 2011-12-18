package com.unhappyrobot;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.input.*;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.List;

import static com.unhappyrobot.input.InputSystem.Keys;

public class Game implements ApplicationListener {
  private OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private BitmapFont font;

  private static List<GameLayer> layers;
  private Matrix4 matrix;

  private Matrix4 hudProjectionMatrix;
  private final GameGrid gameGrid = new GameGrid();
  private GameGridRenderer gameGridRenderer;
  private InputSystem inputSystem;
  private GestureDetector gestureDetector;
  private CameraController cameraController;
  private static TweenManager tweenManager;
  private static Game instance;
  private GridObject mouseRat;

  public void create() {
    instance = this;

    Random.init();
    Tween.setPoolEnabled(true);

    tweenManager = new TweenManager();

    Gdx.graphics.setVSync(true);

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    camera = new OrthographicCamera(width, height);
    spriteBatch = new SpriteBatch(100);
    hudProjectionMatrix = spriteBatch.getProjectionMatrix().cpy();

    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    layers = new ArrayList<GameLayer>();

    font = new BitmapFont(Gdx.files.internal("fonts/menlo_16.fnt"), Gdx.files.internal("fonts/menlo_16.png"), false);

    gameGrid.setGridOrigin(0, 0);
    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(10, 10);
    gameGrid.resizeGrid();
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

//    BackgroundLayer backgroundLayer = new BackgroundLayer("backgrounds/rock1.png");
//    backgroundLayer.position.set(0, 0);
//    backgroundLayer.size.set(20, 5);
//
//    gameGrid.addObject(backgroundLayer);
    CloudLayer cloudLayer = new CloudLayer(gameGrid.getWorldSize());
    layers.add(cloudLayer);

    gameGridRenderer = gameGrid.getRenderer();
    layers.add(gameGridRenderer);

    inputSystem = new InputSystem(camera, gameGrid);
    Gdx.input.setInputProcessor(inputSystem);

    inputSystem.bind(Keys.W, new Action() {
      public void run(float timeDelta) {
        camera.position.add(0, 3, 0);
      }
    });

    inputSystem.bind(Keys.S, new Action() {
      public void run(float timeDelta) {
        camera.position.add(0, -3, 0);
      }
    });

    inputSystem.bind(Keys.G, new Action() {
      public void run(float timeDelta) {
        gameGridRenderer.toggleGridLines();
      }
    });

    inputSystem.bind(Keys.NUM_1, new Action() {
      public void run(float timeDelta) {
        inputSystem.switchTool(GestureTool.PLACEMENT);
        PlacementTool placementTool = (PlacementTool) inputSystem.getCurrentTool();
        placementTool.setup(camera, gameGrid);
      }
    });
  }

  public void render() {
    GL10 gl = Gdx.graphics.getGL10();
    gl.glClearColor(0.73f, 0.925f, 0.984f, 1.0f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL10.GL_BLEND);
    gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    inputSystem.update(deltaTime);
    camera.update();

    spriteBatch.setProjectionMatrix(camera.combined);

    gl.glColor4f(1, 1, 1, 1);
    for (GameLayer layer : layers) {
      layer.render(spriteBatch, camera);
    }

    spriteBatch.setProjectionMatrix(hudProjectionMatrix);
    spriteBatch.begin();
    String infoText = String.format("fps: %d, camera(%.1f, %.1f, %.1f)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom);
    font.draw(spriteBatch, infoText, 5, 23);
    spriteBatch.end();

    update();
  }

  public void update() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    DeferredManager.onGameThread().update(deltaTime);

    for (GameLayer layer : layers) {
      layer.update(deltaTime);
    }

    tweenManager.update();
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    TextureDict.unloadAll();
    spriteBatch.dispose();
    font.dispose();
  }

  public static List<GameLayer> getLayers() {
    return layers;
  }

  public static TweenManager getTweenManager() {
    return tweenManager;
  }

  public static Game getInstance() {
    return instance;
  }
}
