package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.input.InputSystem;

public class Menu extends Table {
  public static final float FADE_DURATION = 0.1f;
  private MenuCloser menuCloser;
  private Actor parent;

  public Menu(Skin guiSkin) {
    super(guiSkin);

    setBackground(guiSkin.getPatch("default-round"));
    menuCloser = new MenuCloser(this);
  }

  public void show(Group parent) {
    this.parent = parent;

    x = 0;
    y = -height + 1;

    parent.addActor(this);
    color.a = 0;
    action(FadeIn.$(FADE_DURATION));

    InputSystem.getInstance().addInputProcessor(menuCloser, 0);
  }

  public void close() {
    InputSystem.getInstance().removeInputProcessor(menuCloser);

    action(FadeOut.$(FADE_DURATION).setCompletionListener(new OnActionCompleted() {
      public void completed(Action action) {
        markToRemove(true);
      }
    }));
  }
}
