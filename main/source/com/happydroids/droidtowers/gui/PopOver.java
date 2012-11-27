/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.TowerAssetManager.texture;
import static com.happydroids.droidtowers.input.InputSystem.Keys.BACK;
import static com.happydroids.droidtowers.input.InputSystem.Keys.ESCAPE;

public class PopOver extends WidgetGroup {
  public static final float INACTIVE_BUTTON_ALPHA = 0.5f;
  public static final float ACTIVE_BUTTON_ALPHA = 0.85f;
  public static final float BUTTON_FADE_DURATION = 0.125f;

  protected Texture triangle;
  private int arrowAlignment;
  private final Texture swatch;
  private final Texture background;
  protected Table content;

  public PopOver() {
    triangle = texture(TowerAssetManager.WHITE_SWATCH_TRIANGLE);
    swatch = texture(TowerAssetManager.WHITE_SWATCH);
    background = TowerAssetManager.texture("hud/window-bg.png");
    background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    content = new Table();
    content.defaults().top().left().space(Display.devicePixel(6));

    setTouchable(Touchable.enabled);
    addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return true;
      }
    });
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

  public Array<Actor> getActors() {
    return content.getChildren();
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    SceneManager.activeScene().effects().drawDropShadow(batch, parentAlpha, this);

    float xOffset = (arrowAlignment & Align.right) != 0 ? getWidth() - triangle.getWidth() - 8 : 8;
    batch.setColor(Colors.ICS_BLUE);
    batch.draw(triangle, getX() + xOffset + 2, getY() + getHeight() + 2, 2, 3, triangle.getWidth() - 4, triangle.getHeight() - 6);
    batch.draw(swatch, getX() - 2, getY() - 2, getWidth() + 4, getHeight() + 4);

    batch.setColor(Colors.DARKER_GRAY);
    batch.draw(triangle, getX() + xOffset, getY() + getHeight() - 4);

    batch.setColor(Color.WHITE);
    batch.draw(background, getX(), getY(), getWidth(), getHeight());
  }

  public int getOffset() {
    return triangle.getHeight();
  }

  public void alignArrow(int arrowAlignment) {
    this.arrowAlignment = arrowAlignment;
  }

  public void toggle(Actor parentWidget, Actor relativeTo) {
    if (!isVisible()) {
      show(parentWidget, relativeTo);
    } else {
      hide();
    }
  }

  protected void show(Actor parentWidget, Actor relativeTo) {
    setVisible(true);
    content.setVisible(true);

    parentWidget.getStage().addActor(this);
    parentWidget.getStage().addActor(content);

    content.pack();
    pack();
    float relativeX = relativeTo.getX() + parentWidget.getX();
    if ((arrowAlignment & Align.right) != 0) {
      setX(relativeX - getWidth() + relativeTo.getWidth() - ((relativeTo.getWidth() - triangle.getWidth()) / 2) + 8);
    } else {
      setX(relativeX + ((relativeTo.getWidth() - triangle.getWidth()) / 2) - 8);
    }
    setY(relativeTo.getY() + parentWidget.getY() - getHeight() - relativeTo.getHeight() / 2);
    content.setX(getX() + Display.devicePixel(10));
    content.setY(getY() + Display.devicePixel(10));

    InputSystem.instance().bind(new int[]{ESCAPE, BACK}, inputCallback);
    InputSystem.instance().addInputProcessor(clickCallback, 0);

    addAction(Actions.fadeIn(BUTTON_FADE_DURATION));
    content.addAction(Actions.fadeIn(BUTTON_FADE_DURATION));
  }

  protected void hide() {
    InputSystem.instance().unbind(new int[]{ESCAPE, BACK}, inputCallback);
    InputSystem.instance().removeInputProcessor(clickCallback);

    addAction(Actions.sequence(Actions.fadeOut(BUTTON_FADE_DURATION), Actions.run(new Runnable() {
      @Override
      public void run() {
        setVisible(false);
      }
    })));

    content.addAction(Actions.sequence(Actions.fadeOut(BUTTON_FADE_DURATION), Actions.run(new Runnable() {
      @Override
      public void run() {
        content.setVisible(false);
      }
    })));
  }

  private final InputCallback inputCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      boolean menuWasVisible = isVisible();
      hide();
      return menuWasVisible;
    }
  };

  private final InputAdapter clickCallback = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      Vector2 touchDown = new Vector2(x, y);
      getStage().screenToStageCoordinates(touchDown);
      stageToLocalCoordinates(touchDown);

      if (hit(touchDown.x, touchDown.y, true) == null) {
        hide();
      }

      return false;
    }
  };

  @Override
  public float getPrefWidth() {
    return content.getPrefWidth() + Display.devicePixel(20);
  }

  @Override
  public float getPrefHeight() {
    return content.getPrefHeight() + Display.devicePixel(20);
  }
}
