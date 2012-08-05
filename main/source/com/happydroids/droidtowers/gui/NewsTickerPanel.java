/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import static com.happydroids.droidtowers.gui.FontManager.Default;

public class NewsTickerPanel extends Table {
  private final Label label;
  private List<String> stories;
  private Iterator<String> storiesIterator;


  public NewsTickerPanel() {
    stories = Lists.newArrayList();
    storiesIterator = Iterables.cycle(stories).iterator();

    label = Default.makeLabel("");

    defaults();
    add(Default.makeLabel("NEWS: ", Color.LIGHT_GRAY)).bottom();
    add(label).expandX();


    addStory("Some droids are stuck in the elevator!");
    addStory("Timmy fell into the well!");
    addStory("Bleh!");

    label.setText(storiesIterator.next());

    label.getColor().a = 0f;

    label.addAction(Actions.forever(Actions.sequence(
                                                            Actions.fadeIn(0.35f),
                                                            Actions.delay(5f),
                                                            Actions.fadeOut(0.35f),
                                                            Actions.run(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                label.setText(storiesIterator.next());
                                                              }
                                                            })
    )));
  }

  private void addStory(String storyText) {
    stories.add(storyText);
    storiesIterator = Iterables.cycle(stories).iterator();
  }

  @Override
  public void act(float delta) {
    super.act(delta);
  }
}
