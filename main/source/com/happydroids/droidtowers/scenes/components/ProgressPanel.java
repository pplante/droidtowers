/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.utils.Random;

import java.util.Set;

public class ProgressPanel extends Table {
  private Label loadingMessage;
  private Label progressLabel;
  private Set<String> messagesUsed;
  private float progressLastChanged;

  public ProgressPanel() {
    super();

    messagesUsed = Sets.newHashSet();

    loadingMessage = FontManager.Roboto32.makeLabel(selectRandomMessage(), Color.WHITE, Align.center);
    loadingMessage.setColor(Color.DARK_GRAY);
    loadingMessage.setAlignment(Align.center);

    progressLabel = FontManager.Roboto64.makeLabel("0%", Color.WHITE, Align.center);
    progressLabel.setColor(Color.DARK_GRAY);
    progressLabel.setAlignment(Align.center);

    row();
    add(loadingMessage).center();
    row();
    add(progressLabel).center();
  }

  private String selectRandomMessage() {
    String msg;
    do {
      msg = STRINGS[Random.randomInt(STRINGS.length - 1)];
    } while (messagesUsed.contains(msg));

    messagesUsed.add(msg);

    return msg;
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    if (progressLastChanged > 3f) {
      progressLastChanged = 0;
      loadingMessage.setText(selectRandomMessage());
    }

    progressLastChanged += delta;
  }

  public synchronized void setProgress(int progress) {
    progressLabel.setText(progress + "%");
    Thread.yield();
  }

  public static final String[] STRINGS = new String[]{
                                                             "reticulating splines...",
                                                             "manufacturing robots",
                                                             "tickling random number generator",
                                                             "wasting your time",
                                                             "infinite recursion",
                                                             "are we there yet?",
                                                             "solving world hunger",
                                                             "booting skynet...SUCCESS!",
                                                             "GLaDOS loves you.",
                                                             "priming buttons for clicking",
                                                             "calculating shipping and handling",
                                                             "contacting the authorities",
                                                             "I'm still alive...",
                                                             "downloading pictures of cats",
                                                             "spinning up ftl drives",
                                                             "so, uhh...how are you?",
                                                             "its going to be\na beautiful day!",
                                                             "de-fuzzing logic pathways",
                                                             "cleaning the tubes",
  };


  public void setProgress(float v) {
    setProgress((int) v);
  }
}
