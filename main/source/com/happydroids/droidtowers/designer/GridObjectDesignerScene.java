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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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

import static com.happydroids.droidtowers.TowerAssetManager.ninePatchDrawable;

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
    scrollPane.setFadeScrollBars(true);
    scrollPane.setupFadeScrollBars(0.5f, 1f);
    ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
    paneStyle.background = ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.LIGHT_GRAY);
    paneStyle.vScrollKnob = paneStyle.hScrollKnob = ninePatchDrawable("hud/scroll-bar.png", Color.DARK_GRAY, 3, 3, 3, 3);
    scrollPane.setStyle(paneStyle);
    scrollPane.setSize(180, getStage().getHeight());

    LayeredDrawable layers = new LayeredDrawable();
    layers.add(new DropShadow());
    layers.add(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-purple.png")));

    paneStyle.background = layers;

    canvas = new Canvas();
    canvas.setSize(512, 128);

    Group widgetGroup = new Group();
    widgetGroup.addActor(canvas);
    widgetGroup.setSize(512, 128);
    Table widget = new Table();
    widget.pad(512);
    widget.setBackground(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-light.png")));
    widget.add(widgetGroup).center().pad(512);
    final ScrollPane canvasScrollPane = new ScrollPane(widget);
    canvasScrollPane.setStyle(paneStyle);
    canvasScrollPane.setSize(getStage().getWidth() - 180, getStage().getHeight());
    canvasScrollPane.setPosition(180, 0);
    canvasScrollPane.setOverscroll(false);

    canvasScrollPane.addAction(Actions.sequence(Actions.delay(0.15f),
                                                       Actions.run(new Runnable() {
                                                         @Override public void run() {
                                                           canvasScrollPane.setScrollPercentX(0.5f);
                                                           canvasScrollPane.setScrollPercentY(0.5f);
                                                         }
                                                       })));

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
    getStage().draw();
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

      image.addListener(new GridObjectDesignerItemTouchListener(canvas, inputProcessor, region));
    }
  }
}
