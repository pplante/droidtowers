/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.Scene;

public class GridObjectDesignerScene extends Scene {
  private Canvas canvas;
  private DesignerInputAdapter inputProcessor;
  private GestureDetector gestureDetector;
  private ShapeRenderer shapeRenderer;

  public GridObjectDesignerScene() {
    super();
  }

  @Override public void create(Object... args) {
    shapeRenderer = new ShapeRenderer();

    Table sidebar = new Table();
    sidebar.defaults().center().pad(Display.devicePixel(8));


    ScrollPane scrollPane = new ScrollPane(sidebar);
    ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
    paneStyle.background = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.LIGHT_GRAY);
    scrollPane.setStyle(paneStyle);
    scrollPane.setSize(180, getStage().getHeight());

    LayeredDrawable layers = new LayeredDrawable();
    layers.add(new DropShadow());
    layers.add(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-purple.png")));

    paneStyle.background = layers;


    canvas = new Canvas();
    canvas.setSize(512, 128);

    Table widget = new Table();

    widget.setBackground(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-light.png")));
    widget.setFillParent(true);
    widget.add(canvas).center();
    widget.setSize(canvas.getWidth() * 2, canvas.getHeight() * 2);
    ScrollPane canvasScrollPane = new ScrollPane(widget);
    canvasScrollPane.setSize(getStage().getWidth() - 180, getStage().getHeight());
    canvasScrollPane.setPosition(180, 0);
    canvasScrollPane.setOverscroll(true);

    getStage().addActor(canvasScrollPane);
    getStage().addActor(scrollPane);

    cameraController.updateCameraConstraints(new Vector2(128, 128));
    cameraController.panTo(0, 0, false);
    gestureDetector = new GestureDetector(20 * Display.getScaledDensity(), 0.5f, 1, 0.15f, cameraController);

    inputProcessor = new DesignerInputAdapter(canvas, getStage(), getCamera());
    InputSystem.instance().addInputProcessor(inputProcessor, 5);

    addAtlasItemsToSidebar(sidebar, "designer/housing/cheap.txt");
    addAtlasItemsToSidebar(sidebar, "designer/housing/high-class.txt");
  }

  @Override public void pause() {
  }

  @Override public void resume() {
  }

  @Override public void render(float deltaTime) {
//    camera.translate(-90, 0, 0);
//    camera.update();
//    getSpriteBatch().setProjectionMatrix(camera.combined);
//    getSpriteBatch().begin();
//    canvas.draw(getSpriteBatch(), 1f);
//    getSpriteBatch().end();


    shapeRenderer.setProjectionMatrix(getCamera().combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.filledCircle(0, 0, 4);
    shapeRenderer.end();


    shapeRenderer.begin(ShapeRenderer.ShapeType.Rectangle);
    BoundingBox cameraBounds = cameraController.getCameraBounds();
    shapeRenderer.rect(cameraBounds.getMin().x,
                              cameraBounds.getMin().y,
                              cameraBounds.getMax().x * 2,
                              cameraBounds.getMax().y * 2);
    shapeRenderer.end();

    camera.translate(90, 0, 0);
    camera.update();

    getStage().draw();

    shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
    shapeRenderer.setColor(Color.GREEN);
    shapeRenderer.filledCircle(getStage().getWidth() / 2 + 90, getStage().getHeight() / 2, 4);
    shapeRenderer.end();
  }

  @Override public void dispose() {
    InputSystem.instance().removeInputProcessor(inputProcessor);
    InputSystem.instance().removeInputProcessor(gestureDetector);
  }

  private void addAtlasItemsToSidebar(Table sidebar, final String atlasFileName) {
    TextureAtlas atlas = new TextureAtlas(atlasFileName);
    for (Texture texture : atlas.getTextures()) {
      texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    for (final TextureAtlas.AtlasRegion region : atlas.getRegions()) {
      sidebar.row();
      final Image image = new Image(new TextureRegionDrawable(region), Scaling.fit);
      sidebar.add(image).minWidth(32).minHeight(32);

      image.addListener(new GridObjectDesignerItemTouchListener(inputProcessor, region));
    }
  }
}
