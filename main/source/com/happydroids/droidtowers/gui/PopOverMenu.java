/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;

import java.util.List;

import static com.happydroids.droidtowers.TowerAssetManager.texture;
import static com.happydroids.droidtowers.input.InputSystem.Keys.BACK;
import static com.happydroids.droidtowers.input.InputSystem.Keys.ESCAPE;
import static com.happydroids.droidtowers.platform.Display.scale;

public class PopOverMenu extends WidgetGroup {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.125f;

  protected Texture triangle;
  private int arrowAlignment;
  private final Texture swatch;
  private final Texture background;
  private Table content;

  public PopOverMenu() {
    triangle = texture(TowerAssetManager.WHITE_SWATCH_TRIANGLE);
    swatch = texture(TowerAssetManager.WHITE_SWATCH);
    background = TowerAssetManager.texture("hud/window-bg.png");
    background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    content = new Table();
    content.defaults().top().left().space(scale(6));

    touchable = true;
  }

  public Cell row() {
    return content.row();
  }

  public Cell add() {
    return content.add();
  }

  public Cell add(Actor actor) {
    return content.add(actor);
  }

  public List<Actor> getActors() {
    return content.getActors();
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    float xOffset = (arrowAlignment & Align.RIGHT) != 0 ? width - triangle.getWidth() - 8 : 8;
    batch.setColor(Colors.ICS_BLUE);
    batch.draw(triangle, x + xOffset + 2, y + height + 2, 2, 3, triangle.getWidth() - 4, triangle.getHeight() - 6);
    batch.draw(swatch, x - 2, y - 2, width + 4, height + 4);

    batch.setColor(Colors.DARKER_GRAY);
    batch.draw(triangle, x + xOffset, y + height - 4);

    batch.setColor(Color.WHITE);
    batch.draw(background, x, y, width, height);
  }

  public int getOffset() {
    return triangle.getHeight();
  }

  public void alignArrow(int arrowAlignment) {
    this.arrowAlignment = arrowAlignment;
  }

  public void toggle(Actor parentWidget, Actor relativeTo) {
    visible = !visible;
    content.visible = visible;

    if (visible) {
      show(parentWidget, relativeTo);
    } else {
      hide();
    }
  }

  private void show(Actor parentWidget, Actor relativeTo) {
    parentWidget.getStage().addActor(this);
    parentWidget.getStage().addActor(content);

    content.pack();
    pack();
    float relativeX = relativeTo.x + parentWidget.x;
    if ((arrowAlignment & Align.RIGHT) != 0) {
      x = relativeX - width + relativeTo.width - ((relativeTo.width - triangle.getWidth()) / 2) + 8;
    } else {
      x = relativeX + ((relativeTo.width - triangle.getWidth()) / 2) - 8;
    }
    y = relativeTo.y + parentWidget.y - height - (triangle.getHeight() / 4);
    content.x = x + scale(10);
    content.y = y + scale(10);

    InputSystem.instance().bind(new int[]{ESCAPE, BACK}, inputCallback);
    InputSystem.instance().addInputProcessor(clickCallback, 0);

    action(FadeIn.$(BUTTON_FADE_DURATION));
    content.action(FadeIn.$(BUTTON_FADE_DURATION));
  }

  private void hide() {
    if (!visible) return;

    InputSystem.instance().unbind(new int[]{ESCAPE, BACK}, inputCallback);
    InputSystem.instance().removeInputProcessor(clickCallback);

    action(FadeOut.$(BUTTON_FADE_DURATION).setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        visible = false;
      }
    }));

    content.action(FadeOut.$(BUTTON_FADE_DURATION).setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        content.visible = false;
      }
    }));
  }

  private final InputCallback inputCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      boolean menuWasVisible = visible;
      hide();
      return menuWasVisible;
    }
  };

  private final InputAdapter clickCallback = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      Vector2 touchDown = new Vector2();
      getStage().toStageCoordinates(x, y, touchDown);
      toLocalCoordinates(touchDown);

      if (hit(touchDown.x, touchDown.y) == null) {
        hide();
        return true;
      }

      return false;
    }
  };

  @Override
  public float getPrefWidth() {
    return content.getPrefWidth() + scale(20);
  }

  @Override
  public float getPrefHeight() {
    return content.getPrefHeight() + scale(20);
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    super.touchDown(x, y, pointer);

    return true;
  }
}
