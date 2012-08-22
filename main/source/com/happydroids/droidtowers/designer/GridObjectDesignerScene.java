/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.designer.tasks.SyncDesignerObjectTypeTask;
import com.happydroids.droidtowers.designer.types.DesignerObjectCategory;
import com.happydroids.droidtowers.designer.types.DesignerObjectType;
import com.happydroids.droidtowers.designer.types.DesignerObjectTypeFactory;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.Scene;

import java.util.List;

import static com.happydroids.droidtowers.TowerAssetManager.ninePatchDrawable;

public class GridObjectDesignerScene extends Scene {
  private Canvas canvas;
  private DesignerInputAdapter inputProcessor;
  private Table sidebar;
  private TextureAtlas objectTypeAtlas;

  public GridObjectDesignerScene() {
    super();
  }

  @Override public void create(Object... args) {
    TowerAssetManager.assetManager().finishLoading();

    sidebar = new Table();
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
    layers.add(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-purple.png")));
    paneStyle.background = layers;

    canvas = new Canvas();
    canvas.setScale(1);
    canvas.setSize(256, 64);

    Table widget = new Table();
    widget.debug();
    widget.pad(180);
    widget.add(canvas);
    final ScrollPane canvasScrollPane = new ScrollPane(widget);
    paneStyle = new ScrollPane.ScrollPaneStyle(paneStyle);
    layers = new LayeredDrawable();
    layers.add(new TiledDrawable(TowerAssetManager.drawable("swatches/modal-noise-light.png")));
    layers.add(new InnerShadow());
    paneStyle.background = layers;
    canvasScrollPane.setStyle(paneStyle);
    canvasScrollPane.setSize(getStage().getWidth() - 180, getStage().getHeight());
    canvasScrollPane.setPosition(180, 0);
    canvasScrollPane.setOverscroll(true, true);

//    canvasScrollPane.addAction(Actions.sequence(Actions.delay(0.15f),
//                                                       Actions.run(new Runnable() {
//                                                         @Override public void run() {
//                                                           canvasScrollPane.setScrollPercentX(0.5f);
//                                                           canvasScrollPane.setScrollPercentY(0.5f);
//                                                         }
//                                                       })));

    getStage().addActor(canvasScrollPane);
    getStage().addActor(scrollPane);

    objectTypeAtlas = new TextureAtlas();
    new SyncDesignerObjectTypeTask(objectTypeAtlas)
            .addPostExecuteRunnable(new AddObjectTypesToSidebar())
            .run();
  }

  @Override public void pause() {
  }

  @Override public void resume() {
  }

  @Override public void render(float deltaTime) {
    getStage().draw();
  }

  @Override public void dispose() {
//    InputSystem.instance().removeInputProcessor(inputProcessor);
  }

  private class AddObjectTypesToSidebar implements Runnable {
    @Override public void run() {
      for (DesignerObjectCategory category : DesignerObjectCategory.values()) {
        List<DesignerObjectType> byCategory = DesignerObjectTypeFactory.instance().findByCategory(category);
        if (!byCategory.isEmpty()) {
          sidebar.row().fillX();
          sidebar.add(FontManager.RobotoBold18.makeLabel(category.name().toUpperCase())).expandX();

          for (DesignerObjectType type : byCategory) {
            TextureAtlas.AtlasRegion region = objectTypeAtlas.findRegion(type.getTypeId());
            sidebar.row();

            final Image image = new Image(new TextureRegionDrawable(region), Scaling.fit);
            sidebar.add(image).size(image.getWidth() * 2, image.getHeight() * 2);

            image.addListener(new GridObjectDesignerItemTouchListener(canvas, region));
          }
        }
      }
    }
  }
}
