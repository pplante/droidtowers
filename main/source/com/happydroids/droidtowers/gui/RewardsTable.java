/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.achievements.Reward;

import java.util.List;

import static com.happydroids.droidtowers.gui.FontManager.Roboto18;

class RewardsTable extends Table {
  public RewardsTable(List<Reward> rewards) {
    defaults().top().left().space(8).fillX();

    add(FontManager.Default.makeLabel("reward", Color.GRAY)).expandX();

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1)).colspan(2);

    for (Reward reward : rewards) {
      addReward(reward);
    }
  }

  @SuppressWarnings("unchecked")
  private void addReward(Reward reward) {
    row().fillX().pad(12, 0, 12, 0);
    Label label = Roboto18.makeLabel(reward.getRewardString(false));
    label.setWrap(true);
    add(label).expandX();

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1)).colspan(3);
  }
}
