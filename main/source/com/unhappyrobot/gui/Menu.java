package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;

import static com.unhappyrobot.input.InputSystem.Keys;

public class Menu extends Table {
  public static final float FADE_DURATION = 0.1f;
  private MenuCloser menuCloser;
  private final static int[] closeKeys = new int[]{Keys.ESCAPE, Keys.BACK};
  private final InputCallback keyCloser;

  public Menu(Skin guiSkin) {
    super(guiSkin);

    setBackground(guiSkin.getPatch("default-round"));
    menuCloser = new MenuCloser(this);
    setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
      }
    });
    keyCloser = new InputCallback() {
      public boolean run(float timeDelta) {
        close();
        return true;
      }
    };
  }

  public void show(Group parent) {
    x = 0;
    y = -height + 1;

    parent.addActor(this);
    color.a = 0;
    action(FadeIn.$(FADE_DURATION));

    InputSystem.instance().addInputProcessor(menuCloser, 0);
    InputSystem.instance().bind(closeKeys, keyCloser);
  }

  public void close() {
    InputSystem.instance().removeInputProcessor(menuCloser);
    InputSystem.instance().unbind(closeKeys, keyCloser);

    action(FadeOut.$(FADE_DURATION).setCompletionListener(new OnActionCompleted() {
      public void completed(Action action) {
        markToRemove(true);
      }
    }));
  }
}
