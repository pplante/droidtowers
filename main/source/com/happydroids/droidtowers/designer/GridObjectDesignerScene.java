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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    addAtlasItemsToSidebar(sidebar, "designer/housing/cheap.txt");
    addAtlasItemsToSidebar(sidebar, "designer/housing/high-class.txt");

    ScrollPane scrollPane = new ScrollPane(sidebar);
    ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
    paneStyle.background = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.LIGHT_GRAY);
    scrollPane.setStyle(paneStyle);
    scrollPane.setSize(180, getStage().getHeight());

    getStage().addActor(scrollPane);

    canvas = new Canvas();
    canvas.setSize(512, 128);
//    canvas.setPosition(-256, -64);
    canvas.setOriginX(180);

    cameraController.updateCameraConstraints(new Vector2(128, 128));
    cameraController.panTo(0, 0, false);
    gestureDetector = new GestureDetector(20 * Display.getScaledDensity(), 0.5f, 1, 0.15f, cameraController);

    inputProcessor = new DesignerInputAdapter(canvas, getStage(), getCamera());
    InputSystem.instance().addInputProcessor(inputProcessor, 5);
    InputSystem.instance().addInputProcessor(gestureDetector, 20);
  }

  @Override public void pause() {
  }

  @Override public void resume() {
  }

  @Override public void render(float deltaTime) {
    getSpriteBatch().begin();
    canvas.draw(getSpriteBatch(), 1f);
    getSpriteBatch().end();

    shapeRenderer.setProjectionMatrix(getCamera().combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.filledCircle(0, 0, 4);
    shapeRenderer.end();


    shapeRenderer.begin(ShapeRenderer.ShapeType.Rectangle);
    BoundingBox cameraBounds = cameraController.getCameraBounds();
    shapeRenderer.rect(cameraBounds.getMin().x, cameraBounds.getMin().y, cameraBounds.getMax().x, cameraBounds.getMax().y);
    shapeRenderer.end();

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

      image.addListener(new InputListener() {
        @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          event.cancel();

          Image selectedItem = new Image(region);
          selectedItem.setScaling(Scaling.none);
          float width = selectedItem.getWidth();
          float height = selectedItem.getHeight();
          if (width < 32) {
            selectedItem.setWidth(32);
          }
          if (height < 32) {
            selectedItem.setHeight(32);
          }
          selectedItem.setOrigin(width / 2, height / 2);
          selectedItem.addAction(Actions.sequence(Actions.scaleTo(1.25f, 1.25f, 0.15f),
                                                         Actions.scaleTo(1f, 1f, 0.15f)));
          selectedItem.setPosition(event.getStageX() - x, event.getStageY() - y);

          event.getStage().addActor(selectedItem);

          inputProcessor.setSelectedItem(selectedItem);
          inputProcessor.setTouchOffset(new Vector2(x, y));
          inputProcessor.setOriginalPosition(new Vector2(event.getStageX(), event.getStageY()));

          return true;
        }
      });
    }
  }


}
