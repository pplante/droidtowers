/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gui.TowerWindow;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;

public class GridObjectDesigner extends TowerWindow {
  private final Canvas canvas;
  private final DesignerInputAdapter inputProcessor;

  public GridObjectDesigner(Stage stage) {
    super("Designer", stage);

    Table sidebar = new Table();
    sidebar.defaults().center().space(Display.devicePixel(8));
    addAtlasItemsToSidebar(sidebar, "designer/housing/cheap.txt");
    addAtlasItemsToSidebar(sidebar, "designer/housing/high-class.txt");

    padding(0);
    debug();
    row().fill();
    ScrollPane scrollPane = new ScrollPane(sidebar);
    ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
    paneStyle.background = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Color.LIGHT_GRAY);
    scrollPane.setStyle(paneStyle);
    add(scrollPane).width(180);
    canvas = new Canvas();
    add(canvas).width(512).height(128).expand().center();

    inputProcessor = new DesignerInputAdapter(canvas);
    InputSystem.instance().addInputProcessor(inputProcessor, 5);

    getContent().addListener(new ActorGestureListener() {
      private float initialZoom = 1f;

      @Override public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);
        initialZoom = canvas.getScaleX();
      }

      @Override public void zoom(InputEvent event, float initialDistance, float distance) {
        float zoom = MathUtils.clamp(initialZoom * distance / initialDistance, 1f, 1.5f);
        canvas.setScale(zoom);
      }
    });

    setDismissCallback(new Runnable() {
      @Override public void run() {
        InputSystem.instance().removeInputProcessor(inputProcessor);
      }
    });
  }

  private void addAtlasItemsToSidebar(Table sidebar, final String atlasFileName) {
    TextureAtlas atlas = new TextureAtlas(atlasFileName);
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
