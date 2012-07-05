/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.TowerAssetManager.sprite;
import static com.happydroids.droidtowers.platform.Display.scale;

public class GridObjectPopOver<T extends GridObject> extends Table {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.125f;
  public static final String CONNECTED_TO_TRANSIT = "Connected to Transit";
  public static final String NOT_CONNECTED_TO_TRANSIT = "Disconnected from Transit";

  private final Sprite triangle;
  protected final T gridObject;
  private final StarRatingBar desirabilityBar;
  private final StarRatingBar noiseBar;
  protected final Label transitLabel;

  public GridObjectPopOver(T gridObject) {
    super();
    visible = false;

    this.gridObject = gridObject;

    touchable = true;
    triangle = sprite(TowerAssetManager.WHITE_SWATCH_TRIANGLE_LEFT);
    triangle.setColor(Color.DARK_GRAY);

    setBackground(TowerAssetManager.ninePatch("hud/dialog-bg.png", Color.WHITE, 1, 1, 1, 1));
    defaults().left().space(scale(6));

    pad(scale(8));

    row();
    add(FontManager.RobotoBold18.makeLabel(gridObject.getName()));

    row().fillX().pad(-8).padTop(0).padBottom(0);
    add(new HorizontalRule()).expandX();

    row();
    transitLabel = FontManager.Default.makeLabel(CONNECTED_TO_TRANSIT);
    add(transitLabel);

    desirabilityBar = makeStarRatingBar("Desirability");
    noiseBar = makeStarRatingBar("Noise");


    setClickListener(new ClickListener() {
      @Override
      public void click(Actor actor, float x, float y) {
      }
    });

//    debug();
  }

  protected StarRatingBar makeStarRatingBar(String labelText) {
    row();
    add(FontManager.Default.makeLabel(labelText));
    row();
    StarRatingBar ratingBar = new StarRatingBar(5f, 5);
    add(ratingBar);

    return ratingBar;
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    super.drawBackground(batch, parentAlpha);

    batch.setColor(0.364f, 0.364f, 0.364f, parentAlpha);
    batch.draw(triangle, x - triangle.getWidth() + 1, y + ((height - triangle.getHeight()) / 2));

    batch.setColor(0.2666f, 0.2666f, 0.2666f, parentAlpha);
    batch.draw(triangle, x - triangle.getWidth() + 2, y + ((height - triangle.getHeight()) / 2));
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    desirabilityBar.setValue(gridObject.getDesirability() * 5f);
    noiseBar.setValue(gridObject.getSurroundingNoiseLevel() * 5f);

    boolean updatedLayout = false;
    if (gridObject.isConnectedToTransport() && !transitLabel.getText().equals(CONNECTED_TO_TRANSIT)) {
      transitLabel.setText(CONNECTED_TO_TRANSIT);
      transitLabel.setColor(Color.WHITE);
      updatedLayout = true;
    } else if (!gridObject.isConnectedToTransport() && !transitLabel.getText().equals(NOT_CONNECTED_TO_TRANSIT)) {
      transitLabel.setText(NOT_CONNECTED_TO_TRANSIT);
      transitLabel.setColor(Color.RED);
      updatedLayout = true;
    }

    if (updatedLayout) {
      invalidateHierarchy();
      pack();
    }
  }
}
